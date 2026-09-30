/**
 * adminStudentIDCard.js  —  Administration -> ID card Generation.
 *
 * Loaded by idCardGenerationBuild() in common.js after
 * js/id-cards/qrcode.js, js/id-cards/id-card-templates.js and
 * js/id-cards/id-cards.js. Renders the page shell into #wrapper, the same way
 * the other Orb pages do, and mounts the ID card generator inside it.
 *
 * The school, its details and its card colours (taken from the crest) are all
 * resolved on the server from the signed-in user - see IdCardController.
 */
(function () {
    'use strict';

    var wrapper = document.getElementById('wrapper');
    if (!wrapper) return;

    wrapper.innerHTML = [
        '<div class="fc-page" id="idcPage">',
        '<header class="fc-header">',
        '<div>',
        '<h1><i class="fas fa-id-card"></i> ID Card Generation</h1>',
        '<p>Print student ID cards in your school\'s colours, taken from your crest</p>',
        '</div>',
        '</header>',
        '<div class="idc-host"><section id="idCardsApp" aria-label="Student ID cards"></section></div>',
        '</div>'
    ].join('');

    if (!window.OrbIdCards || typeof window.OrbIdCards.mount !== 'function') {
        document.getElementById('idCardsApp').textContent =
            'The ID card generator didn\'t load. Refresh the page and try again.';
        return;
    }

    window.OrbIdCards.mount(document.getElementById('idCardsApp'));
})();
