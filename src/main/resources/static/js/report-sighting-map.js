// report-sighting-map.js
// Responsible for map picker behavior on the report-sighting page.
// Uses Leaflet to allow the user to pick a location and populate latitude/longitude inputs.
// Also supports "Use my location" button and optional reverse-geocoding via Nominatim.

(function() {
    // Only run on pages that have the picker map
    const mapEl = document.getElementById('picker-map');
    if (!mapEl) return;

    // Default center (fallback) - can be adjusted to your region
    const DEFAULT_CENTER = [51.5, -0.12];
    const DEFAULT_ZOOM = 5;

    // Create map
    const map = L.map('picker-map', { scrollWheelZoom: false }).setView(DEFAULT_CENTER, DEFAULT_ZOOM);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
    }).addTo(map);

    // Start marker at center
    let marker = L.marker(DEFAULT_CENTER, { draggable: true }).addTo(map);

    const latInput = document.getElementById('latitude');
    const lngInput = document.getElementById('longitude');
    const locationInput = document.getElementById('location');
    const useLocationBtn = document.getElementById('use-location');

    function updateInputs(lat, lng, panTo = false) {
        if (latInput) latInput.value = Number(lat).toFixed(6);
        if (lngInput) lngInput.value = Number(lng).toFixed(6);
        if (panTo) map.setView([lat, lng], 13);
    }

    // When marker is dragged, update inputs and reverse-geocode
    marker.on('dragend', function(e) {
        const p = marker.getLatLng();
        updateInputs(p.lat, p.lng);
        reverseGeocode(p.lat, p.lng);
    });

    // Clicking on the map moves the marker
    map.on('click', function(e) {
        marker.setLatLng(e.latlng);
        updateInputs(e.latlng.lat, e.latlng.lng);
        reverseGeocode(e.latlng.lat, e.latlng.lng);
    });

    // If inputs change manually, update marker position
    function parseAndSetMarker() {
        const lat = parseFloat(latInput.value);
        const lng = parseFloat(lngInput.value);
        if (!isNaN(lat) && !isNaN(lng) && Math.abs(lat) <= 90 && Math.abs(lng) <= 180) {
            marker.setLatLng([lat, lng]);
            map.setView([lat, lng], 13);
        }
    }

    if (latInput) latInput.addEventListener('change', parseAndSetMarker);
    if (lngInput) lngInput.addEventListener('change', parseAndSetMarker);

    // Use browser geolocation to set marker
    if (useLocationBtn) {
        useLocationBtn.addEventListener('click', function() {
            if (!navigator.geolocation) {
                alert('Geolocation is not supported by your browser.');
                return;
            }
            useLocationBtn.disabled = true;
            useLocationBtn.textContent = 'Finding...';
            navigator.geolocation.getCurrentPosition(function(pos) {
                const lat = pos.coords.latitude;
                const lng = pos.coords.longitude;
                marker.setLatLng([lat, lng]);
                updateInputs(lat, lng, true);
                reverseGeocode(lat, lng);
                useLocationBtn.disabled = false;
                useLocationBtn.textContent = 'Use my location';
            }, function(err) {
                useLocationBtn.disabled = false;
                useLocationBtn.textContent = 'Use my location';
                alert('Could not get your location: ' + err.message);
            });
        });
    }

    // Reverse geocode using Nominatim (OpenStreetMap). For production, prefer server-side
    // implementation with caching to avoid rate limits. Respect Nominatim usage policy.
    let lastReverse = 0;
    function reverseGeocode(lat, lng) {
        const now = Date.now();
        // throttle to 1 request per second
        if (now - lastReverse < 1100) return;
        lastReverse = now;
        const url = `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${encodeURIComponent(lat)}&lon=${encodeURIComponent(lng)}`;
        fetch(url, { headers: { 'Accept': 'application/json', 'User-Agent': 'ufo-sightings-app' }})
            .then(res => {
                if (!res.ok) throw new Error('Reverse geocode failed');
                return res.json();
            })
            .then(data => {
                if (data && data.display_name && locationInput) {
                    locationInput.value = data.display_name;
                }
            })
            .catch(err => {
                // silent fail; don't block user
                console.warn('Reverse geocode error', err);
            });
    }

    // If latitude/longitude already prefilled on page load, set marker there
    (function initFromInputs() {
        const lat = parseFloat(latInput?.value);
        const lng = parseFloat(lngInput?.value);
        if (!isNaN(lat) && !isNaN(lng)) {
            marker.setLatLng([lat, lng]);
            map.setView([lat, lng], 13);
        }
    })();
})();

