function loadScript(src) {
  return new Promise((resolve, reject) => {
    const script = document.createElement('script');
    script.src = src;
    script.onload = resolve;
    script.onerror = () => reject(new Error(`Failed to load ${src}`));
    document.head.appendChild(script);
  });
}

async function loadAmCharts() {
  const basePath = 'vendor/amcharts5/'; // Update path if needed
  const scripts = [
    'index.js',       // Core library
    'xy.js',          // XY charts (e.g., line, column)
    'radar.js',       // Radar charts
    'hierarchy.js',       // Radar charts
    'percent.js',       // pie charts
    'themes/Animated.js' // Animated theme
  ];

  const basePath1 = 'scripts/'; // Update path if needed
      const scripts1 = [
        'charts1.js'
      ];

  try {
    for (const script of scripts) {
      await loadScript(`${basePath}${script}`);
    }

    // ✅ Load chart script after all amCharts scripts
    await loadScript("scripts/charts1.js");

    // ✅ Now safely call the chart function
   /* if (typeof initRadarChart === 'function') {
      initRadarChart(); // safe to run now
    } else {
      console.error("initRadarChart is not defined!");
    }*/

    console.log('All scripts loaded and chart initialized!');
  } catch (error) {
    console.error('Error loading amCharts:', error);
  }
}

// Start loading
loadAmCharts();