let currentCat = null;
let currentMode = 'cat'; // 'cat' or 'dog'
let breedChart = null;


async function loadAnimal() {
  const status = document.getElementById('status');
  const err = document.getElementById('error');
  const loader = document.getElementById('loader');
  const img = document.getElementById('cat-img');
  const info = document.getElementById('info');
  const card = document.getElementById('card');
  const minFadeMs = 180;
  const startTs = performance.now();

  err.style.display = 'none';
  status.textContent = currentMode === 'cat' ? 'Fetching today’s cat…' : 'Fetching today’s dog…';
  document.getElementById('refresh').disabled = true;
  loader.classList.add('visible');
  card.classList.add('loading');
  img.classList.remove('show');
  info.classList.remove('show');

  try {
    const endpoint = currentMode === 'cat' ? '/api/cat-of-the-day' : '/api/dog-of-the-day';
    const res = await fetch(endpoint);
    if (!res.ok) throw new Error('API error ' + res.status);
    const data = await res.json();
    currentCat = data; // reusing variable name for simplicity, though it holds either
    console.log('Fetched data:', data);

    const breed = (data.breeds && data.breeds.length > 0) ? data.breeds[0] : {};
    const name = breed.name || (currentMode === 'cat' ? 'Mystery Cat' : 'Mystery Dog');

    document.getElementById('cat-name').textContent = name;
    document.getElementById('cat-desc').textContent = breed.temperament || '';

    const wikiLink = document.getElementById('wikipedia-link');
    // Dog API doesn't always have wikipedia_url, Cat API usually does
    if (breed.wikipedia_url) {
      wikiLink.textContent = 'Learn more on Wikipedia';
      wikiLink.href = breed.wikipedia_url;
    } else {
      wikiLink.textContent = '';
    }

    img.src = data.imageUrl || '';
    img.alt = (breed.name ? breed.name + ' — ' : '') + (currentMode === 'cat' ? 'Cat of the day' : 'Dog of the day');
    status.textContent = currentMode === 'cat' ? 'Here’s your daily feline friend!' : 'Here’s your daily canine companion!';

    if (img.complete) {
      img.classList.add('show');
      info.classList.add('show');
    } else {
      img.onload = () => {
        img.classList.add('show');
        info.classList.add('show');
      };
      img.onerror = () => {
        info.classList.add('show');
      };
    }
  } catch (e) {
    err.textContent = 'Failed to load: ' + e.message;
    err.style.display = 'block';
    status.textContent = 'Unable to fetch.';
    info.classList.add('show');
  } finally {
    document.getElementById('refresh').disabled = false;
    loader.classList.remove('visible');
    const elapsed = performance.now() - startTs;
    const delay = Math.max(0, minFadeMs - elapsed);
    setTimeout(() => card.classList.remove('loading'), delay);
  }
}

document.getElementById('refresh').addEventListener('click', loadAnimal);

// Mode switching
document.getElementById('mode-cat').addEventListener('click', () => {
  if (currentMode === 'cat') return;
  currentMode = 'cat';
  document.getElementById('mode-cat').classList.add('active');
  document.getElementById('mode-dog').classList.remove('active');
  loadAnimal();
});

document.getElementById('mode-dog').addEventListener('click', () => {
  if (currentMode === 'dog') return;
  currentMode = 'dog';
  document.getElementById('mode-dog').classList.add('active');
  document.getElementById('mode-cat').classList.remove('active');
  loadAnimal();
});

loadAnimal();

// Open modal with breed details
function openBreedModal() {
  if (!currentCat || !currentCat.breeds || currentCat.breeds.length === 0) return;
  const breed = currentCat.breeds[0];
  document.getElementById('breed-description').textContent = breed.description || '';

  // Render Chart or Dog Info
  const chartContainer = document.querySelector('.chart-container');
  const bredForEl = document.getElementById('stat-bred-for');
  const breedGroupEl = document.getElementById('stat-breed-group');
  const breedBredFor = document.getElementById('breed-bred-for');
  const breedGroup = document.getElementById('breed-group');

  if (currentMode === 'cat') {
    // Show Chart, Hide Dog specific fields
    chartContainer.style.display = 'block';
    bredForEl.classList.add('hidden');
    breedGroupEl.classList.add('hidden');

    const ctx = document.getElementById('breed-chart').getContext('2d');
    if (breedChart) {
      breedChart.destroy();
    }

    breedChart = new Chart(ctx, {
      type: 'radar',
      data: {
        labels: ['Adaptability', 'Energy', 'Affection', 'Intelligence', 'Vocalisation'],
        datasets: [{
          label: breed.name,
          data: [
            breed.adaptability ?? 0,
            breed.energy_level ?? 0,
            breed.affection_level ?? 0,
            breed.intelligence ?? 0,
            breed.vocalisation ?? 0
          ],
          backgroundColor: 'rgba(34, 211, 238, 0.2)', // Cyan accent with opacity
          borderColor: 'rgba(34, 211, 238, 1)',
          borderWidth: 2,
          pointBackgroundColor: 'rgba(34, 211, 238, 1)',
          pointBorderColor: '#fff',
          pointHoverBackgroundColor: '#fff',
          pointHoverBorderColor: 'rgba(34, 211, 238, 1)'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          r: {
            angleLines: {
              color: 'rgba(255, 255, 255, 0.1)'
            },
            grid: {
              color: 'rgba(255, 255, 255, 0.1)'
            },
            pointLabels: {
              color: '#e5e7eb',
              font: {
                size: 12
              }
            },
            ticks: {
              backdropColor: 'transparent',
              color: 'rgba(255, 255, 255, 0.5)',
              stepSize: 1
            },
            suggestedMin: 0,
            suggestedMax: 5
          }
        },
        plugins: {
          legend: {
            display: false
          }
        }
      }
    });
  } else {
    // Dog Mode: Hide Chart, Show Dog specific fields
    chartContainer.style.display = 'none';
    if (breed.bred_for) {
      bredForEl.classList.remove('hidden');
      breedBredFor.textContent = breed.bred_for;
    } else {
      bredForEl.classList.add('hidden');
    }

    if (breed.breed_group) {
      breedGroupEl.classList.remove('hidden');
      breedGroup.textContent = breed.breed_group;
    } else {
      breedGroupEl.classList.add('hidden');
    }
  }

  document.getElementById('breed-life-span').textContent = breed.life_span ?? '';
  document.getElementById('breed-origin').textContent = breed.origin ?? '';
  document.getElementById('breed-temperament').textContent = breed.temperament ?? '';
  const wikiLink = document.getElementById('breed-wiki-link');
  if (breed.wikipedia_url) {
    wikiLink.href = breed.wikipedia_url;
    wikiLink.style.display = 'inline';
  } else {
    wikiLink.href = '#';
    wikiLink.style.display = 'none';
  }
  document.getElementById('breed-modal').classList.remove('hidden');
}

function closeBreedModal() {
  document.getElementById('breed-modal').classList.add('hidden');
}

// Attach listeners
document.getElementById('cat-img').addEventListener('click', openBreedModal);
document.querySelector('.modal-close').addEventListener('click', closeBreedModal);
document.getElementById('breed-modal').addEventListener('click', (e) => {
  if (e.target === e.currentTarget) closeBreedModal();
});
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') closeBreedModal();
});
