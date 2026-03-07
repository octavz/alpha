const https = require('https');
const fs = require('fs');

const url = 'http://localhost:3000/v3/api-docs';
const file = 'openapi.json';

https.get(url, (res) => {
  let data = '';
  res.on('data', chunk => data += chunk);
  res.on('end', () => {
    fs.writeFileSync(file, data);
    console.log(`OpenAPI spec saved to ${file}`);
  });
}).on('error', (e) => {
  console.error('Error fetching OpenAPI spec:', e);
  process.exit(1);
});
