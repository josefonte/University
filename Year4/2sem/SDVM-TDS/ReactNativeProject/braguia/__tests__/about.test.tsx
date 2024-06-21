import React from 'react';
import { render, fireEvent } from '@testing-library/react-native';
import About from '../About'; // Importe o componente que você deseja testar

describe('<About />', () => {
  test('Verifica se o pop-up do Google Maps é exibido corretamente quando não instalado', () => {
    // Simula o comportamento onde o Google Maps não está instalado
    jest.spyOn(Linking, 'canOpenURL').mockImplementationOnce(() => Promise.resolve(false));

    const { getByText } = render(<About />);

    // Verifica se o texto do pop-up é renderizado corretamente
    expect(getByText('Parece que o Google Maps não está instalado. Instale para uma melhor experiência.')).toBeTruthy();
  });

  test('Verifica se o pop-up do Google Maps não é exibido quando já instalado', () => {
    // Simula o comportamento onde o Google Maps está instalado
    jest.spyOn(Linking, 'canOpenURL').mockImplementationOnce(() => Promise.resolve(true));

    const { queryByText } = render(<About />);

    // Verifica se o texto do pop-up não está presente
    expect(queryByText('Parece que o Google Maps não está instalado. Instale para uma melhor experiência.')).toBeNull();
  });

  // Adicione mais testes conforme necessário para cobrir outros aspectos do componente
});
