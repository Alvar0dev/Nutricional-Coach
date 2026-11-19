import { API_BASE_URL } from './api.config';

describe('API configuration', () => {
  it('uses relative /api/v1 base path for local proxy and Nginx reverse proxy', () => {
    expect(API_BASE_URL).toBe('/api/v1');
  });
});
