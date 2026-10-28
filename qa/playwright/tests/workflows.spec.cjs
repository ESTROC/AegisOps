const { test, expect } = require('@playwright/test');
const username = process.env.TEST_ADMIN_USERNAME || process.env.BOOTSTRAP_ADMIN_USERNAME;
const password = process.env.TEST_ADMIN_PASSWORD || process.env.BOOTSTRAP_ADMIN_PASSWORD;

async function signIn(page) {
  if (!username || !password) throw new Error('Set TEST_ADMIN_USERNAME and TEST_ADMIN_PASSWORD');
  await page.goto('/');
  await page.getByLabel('USERNAME').fill(username);
  await page.getByLabel('PASSWORD').fill(password);
  await page.getByRole('button', { name: /Sign in securely/ }).click();
  await expect(page.getByRole('heading', { name: /Incident operations/ })).toBeVisible();
}

test('authorized login and ticket workflow with search and audit', async ({ page }) => {
  await signIn(page);
  await page.getByRole('button', { name: /New ticket/ }).click();
  const form = page.getByRole('dialog', { name: 'Create ticket' });
  await expect(form).toBeVisible();
  const uniqueTitle = 'Playwright QA Incident ' + Date.now();
  await form.getByLabel('TITLE').fill(uniqueTitle);
  await form.getByLabel('SERVICE / SYSTEM').fill('qa-e2e-service');
  await form.getByLabel('DESCRIPTION').fill('Reproducible synthetic browser regression record');
  await form.getByRole('button', { name: /Create ticket/ }).click();
  await expect(form).not.toBeVisible();
  const search = page.getByPlaceholder('Search title or service...');
  await search.fill(uniqueTitle);
  await expect(page.getByText(uniqueTitle).first()).toBeVisible();
  await page.getByText(uniqueTitle).first().click();
  await expect(page.getByRole('dialog', { name: 'Ticket details' })).toBeVisible();
  await expect(page.getByText('Immutable-style audit history')).toBeVisible();
});

test('invalid credentials return an accessible error without creating a session', async ({ page }) => {
  await page.goto('/');
  await page.getByLabel('USERNAME').fill('user-does-not-exist');
  await page.getByLabel('PASSWORD').fill('invalid-password');
  await page.getByRole('button', { name: /Sign in securely/ }).click();
  await expect(page.getByRole('alert')).toBeVisible();
  await expect(page.getByRole('button', { name: /Sign in securely/ })).toBeVisible();
});

test('API enforces viewer read-only permissions and input validation', async ({ request, baseURL }) => {
  if (!username || !password) throw new Error('Missing test admin credentials');
  const auth = await request.post(baseURL + '/api/auth/login', { data: { username, password } });
  expect(auth.ok()).toBeTruthy();
  const admin = (await auth.json()).accessToken;
  const viewerName = 'qa-viewer-' + Date.now();
  const viewerPassword = 'Temporary-Viewer-Password-2026!';
  const created = await request.post(baseURL + '/api/admin/users', {
    headers: { Authorization: 'Bearer ' + admin },
    data: { username: viewerName, password: viewerPassword, role: 'VIEWER' }
  });
  expect(created.status()).toBe(201);
  const viewerAuth = await request.post(baseURL + '/api/auth/login', {
    data: { username: viewerName, password: viewerPassword }
  });
  expect(viewerAuth.ok()).toBeTruthy();
  const viewer = (await viewerAuth.json()).accessToken;
  const blocked = await request.post(baseURL + '/api/tickets', {
    headers: { Authorization: 'Bearer ' + viewer },
    data: { type: 'INCIDENT', title: 'Forbidden write', description: 'The viewer may not create tickets',
      serviceName: 'qa-e2e-service', priority: 'P2' }
  });
  expect(blocked.status()).toBe(403);
  const list = await request.get(baseURL + '/api/tickets?size=5', { headers: { Authorization: 'Bearer ' + viewer } });
  expect(list.ok()).toBeTruthy();
  const malformed = await request.post(baseURL + '/api/tickets', {
    headers: { Authorization: 'Bearer ' + admin },
    data: { type: 'INCIDENT', title: '', description: 'Invalid payload',
      serviceName: 'qa-e2e-service', priority: 'P2' }
  });
  expect(malformed.status()).toBe(400);
});
