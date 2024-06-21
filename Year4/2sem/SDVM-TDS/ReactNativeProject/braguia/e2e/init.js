const { init, cleanup } = require('detox');
const config = require('../package.json').detox;
const adapter = require('detox/runners/jest/adapter');

jest.setTimeout(120000);

beforeAll(async () => {
  await init(config);
});

beforeEach(async () => {
  await adapter.beforeEach();
});

afterAll(async () => {
  await cleanup();
});
