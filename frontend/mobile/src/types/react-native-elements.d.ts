import 'react-native-elements';

declare module 'react-native-elements' {
  import { CardProps } from 'react-native-elements/dist/card/Card';
  interface CardProps {
    children?: React.ReactNode;
  }
}
