import React from 'react';
import { View, Text, Button } from 'react-native';

describe('Example', () => {
    beforeAll(async () => {
      await device.launchApp();
    });
  
    it('should have welcome screen', async () => {
      await expect(element(by.id('welcome'))).toBeVisible();
    });
  
    it('should show hello screen after tap', async () => {
      await element(by.id('hello_button')).tap();
      await expect(element(by.id('hello'))).toBeVisible();
    });
  
    it('should show world screen after tap', async () => {
      await element(by.id('world_button')).tap();
      await expect(element(by.id('world'))).toBeVisible();
    });
  });
  