// Shared helper for triggering the paid plan-upgrade flow: fetch a quote,
// confirm the price with the user, then launch Paystack. Used by both the
// pre-order/institution-update form (orbsPreOrder.html) and the gated-feature
// redirect page (upgrade-required.html), so there's one implementation of
// "how do we charge for a plan upgrade" instead of two that can drift apart.
//
// Requires: jQuery, SweetAlert v1 (swal), Paystack inline.js, and the usual
// _csrf / _csrf_header meta tags to already be on the page.
//
// The actual plan change never happens here or anywhere else client-side -
// it only happens once InstitutionService#upgradeSubscriptionPaymentStatus
// verifies the payment server-side. This just gets the payment started and
// reports whether the user completed the Paystack popup.

const SUBSCRIPTION_PLAN_LEVELS = {STARTER: 0, GROWTH: 1, ENTERPRISE: 2};

function subscriptionPlanLevel(label) {
    if (!label) {
        return 0;
    }
    const normalized = label.toString().trim().toLowerCase();
    if (normalized.includes('enterprise') || normalized.includes('professional')) {
        return 2;
    }
    if (normalized.includes('growth') || normalized.includes('standard')) {
        return 1;
    }
    return 0;
}

function subscriptionPlanCode(label) {
    const level = subscriptionPlanLevel(label);
    return level === 2 ? 'ENTERPRISE' : level === 1 ? 'GROWTH' : 'STARTER';
}

function isSubscriptionUpgrade(currentLabel, targetLabel) {
    return subscriptionPlanLevel(targetLabel) > subscriptionPlanLevel(currentLabel);
}

/**
 * Fetches a quote, confirms the price with the user, then launches Paystack.
 * Resolves true only if the Paystack popup reported a completed payment;
 * resolves false if the quote couldn't be fetched, the user cancelled the
 * confirmation, or they closed the Paystack popup without paying.
 */
function subscriptionCsrfHeaders() {
    const headers = {'Content-Type': 'application/json', 'Accept': 'application/json'};
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-XSRF-TOKEN';
    if (csrfToken) {
        headers[csrfHeader] = csrfToken;
    }
    return headers;
}

/**
 * Fetches an upgrade quote without launching payment. Used to show the
 * price on the review step before the user commits to submitting the form.
 * Returns null (and shows nothing) on failure - callers decide how loud
 * to be about that, since this is often called opportunistically.
 */
async function fetchUpgradeQuote({institutionCode, targetPlan}) {
    if (!institutionCode || !targetPlan) {
        return null;
    }
    try {
        const response = await fetch('getUpgradeQuote', {
            method: 'POST',
            headers: subscriptionCsrfHeaders(),
            credentials: 'include',
            body: JSON.stringify({institutionCode: institutionCode, targetPlan: subscriptionPlanCode(targetPlan)}),
        });
        const body = await response.json().catch(() => null);
        if (!response.ok) {
            return null;
        }
        return body;
    } catch (err) {
        return null;
    }
}

async function initiateUpgradePayment({institutionCode, email, targetPlan}) {
    if (!institutionCode || !email) {
        await swalPromise({
            title: 'Cannot start payment',
            text: 'Missing institution or email details - try reloading the page.',
            type: 'error',
        });
        return false;
    }

    const headers = subscriptionCsrfHeaders();

    let quote;
    try {
        const response = await fetch('getUpgradeQuote', {
            method: 'POST',
            headers,
            credentials: 'include',
            body: JSON.stringify({institutionCode: institutionCode, targetPlan: subscriptionPlanCode(targetPlan)}),
        });
        const body = await response.json().catch(() => null);
        if (!response.ok) {
            const message = typeof body === 'string' && body.length > 0 ? body : 'Could not fetch pricing for that plan.';
            throw new Error(message);
        }
        quote = body;
    } catch (err) {
        await swalPromise({
            title: 'Could not start upgrade',
            text: err.message || 'Something went wrong fetching pricing.',
            type: 'error',
        });
        return false;
    }

    const confirmed = await swalConfirmPromise({
        title: 'Confirm upgrade',
        text: 'Upgrading to ' + quote.targetPlan + ' for ' + quote.population + ' students costs GHS '
            + quote.totalAmount + ' (GHS ' + quote.ratePerStudent + '/student). Continue to payment?',
        type: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Pay with Paystack',
        cancelButtonText: 'Cancel',
    });

    if (!confirmed) {
        return false;
    }

    return new Promise((resolve) => {
        const handler = PaystackPop.setup({
            key: document.querySelector('meta[name="paystack-public-key"]').getAttribute('content'),
            email: email,
            amount: quote.amountInSubunit,
            currency: quote.currency || 'GHS',
            ref: 'UPGRADE_' + institutionCode + '_' + new Date().getTime(),
            metadata: {
                custom_fields: [
                    {display_name: 'institutionCode', variable_name: 'institutionCode', value: institutionCode},
                    {display_name: 'targetPlan', variable_name: 'targetPlan', value: quote.targetPlan},
                ],
            },
            onClose: function () {
                resolve(false);
            },
            callback: function () {
                resolve(true);
            },
        });
        handler.openIframe();
    });
}

// Thin promise wrappers around SweetAlert v1's callback-style API.
function swalPromise(options) {
    return new Promise((resolve) => swal(options, () => resolve()));
}

function swalConfirmPromise(options) {
    return new Promise((resolve) => swal(options, (isConfirm) => resolve(!!isConfirm)));
}
