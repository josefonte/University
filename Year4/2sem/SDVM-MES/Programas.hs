module Programas where

import Gramatica
import Gramatica (Exp (Great, GreatEqual, MinorEqual))
import Test.QuickCheck (PrintableString (PrintableString))

programa1 =
  PicoC
    [ Atrib "result" (Const 1),
      While
        (Minor (Var "g") (Var "r"))
        [ PRINT (Str ("Dentro do ciclo While(g>r)")),
          (Atrib "result" (Mul (Var "result") (Const 2))),
          PRINT (Str ("Atualizei o Result:")),
          PRINT (Var "result"),
          (Atrib "g" (Add (Var "g") (Const 1))),
          PRINT (Str ("Atualizei o g:")),
          PRINT (Var "g")
        ],
      PRINT (Str ("------------------------FIM DO PROGRAMA------------------------"))
    ]

-- result = 1;
-- while (g > r){
--     result = result * 2;
--     g = g + 1;
-- }

-- programa2 = PicoC [
--                     Atrib "result" (Const 0),
--                     Atrib "g" (Const 1),
--                     While (MinorEqual (Var "g") (Var "n")) [
--                         Atrib "result" (Add (Var "result") (Var "g")),
--                         Atrib "g" (Add (Var "g") (Const 1))
--                     ]
--                 ]

programa2 =
  PicoC
    [ ITE
        (Great (Add (Var "g") (Const 5)) (Var "n"))
        [Atrib "result" (Var "g")]
        [Atrib "result" (Var "n")]
    ]

-- soma de 1 até g
-- result = 0;
-- g = 1;
-- while (g <= n){
--     result = result + g;
--     g = g + 1;
-- }

-- programa3 = PicoC[While (Minor (Var "g") (Const 10))
--                    [
--                        ITE (Great (Var "g") (Const 5))
--                        [Atrib "result" (Add (Var "result") (Const 1))]
--                        [Atrib "result" (Add (Var "result") (Var "r"))],
--                        Atrib "g" (Add (Var "g") (Const 1))
--                    ]
--                ]

-- while (g < 10){
--     if (g>5){
--         result = result + 1
--     }
--     else {
--         result = result + r
--     }
--     g = g + 1
-- }

programa3 =
  PicoC
    [ While
        (Minor (Var "g") (Const 10))
        [ PRINT (Var "g"),
          ITE
            (Great (Var "g") (Const 5))
            [ PRINT (Str ("Entrei na condicao " ++ "if(g>5)")),
              Atrib "result" (Add (Var "result") (Const 1))
            ]
            [ PRINT (Str ("Entrei na condicao " ++ "else(g<=5)")),
              Atrib "result" (Add (Var "result") (Var "r"))
            ],
          Atrib "g" (Add (Var "g") (Const 1))
        ]
    ]

programaComplexo =
  PicoC
    [ Atrib "x" (Const 0),
      Atrib "y" (Const 5),
      While
        (Minor (Var "x") (Const 20))
        [ PRINT (Var "x"),
          ITE
            (GreatEqual (Var "x") (Const 10))
            [ PRINT (Str ("Entrou na condicaoo " ++ "if(x >= 10)")),
              Atrib "y" (Mul (Var "y") (Const 2)),
              ITE
                (Equals (Var "y") (Const 20))
                [ PRINT (Str ("Entrou na condicao " ++ "if(y == 20)")),
                  Atrib "result" (Add (Var "result") (Const 1))
                ]
                [ PRINT (Str ("Entrou na condicao " ++ "else(y != 20)")),
                  Atrib "result" (Add (Var "result") (Var "z"))
                ]
            ]
            [ PRINT (Str ("Entrou na condicao " ++ "else(x < 10)")),
              Atrib "y" (Sub (Var "y") (Const 1)),
              While
                (Minor (Var "y") (Const 0))
                [ PRINT (Str ("Entrou no loop interno")),
                  Atrib "y" (Add (Var "y") (Const 2)),
                  PRINT (Var "y")
                ]
            ],
          Atrib "x" (Add (Var "x") (Const 1)),
          COMS "Comentario de teste"
        ],
      PRINT (Str "Final do programa"),
      PRINT (Var "result")
    ]

d = PicoC [PRINT (Str "adeus")]

d2 = PicoC [PRINT (Add (Const 1) (Const 2))]

d3 = PicoC [PRINT (Sub (Const 2) (Const 3))]

d4 = PicoC [PRINT (Mul (Const 1) (Const 2))]

d5 = PicoC [PRINT (Div (Const 1) (Const 2))]

d6 = PicoC [PRINT (Great (Const 2) (Const 3))]

d7 = PicoC [PRINT (GreatEqual (Const 1) (Const 2))]

d8 = PicoC [PRINT (Minor (Const 1) (Const 2))]

d9 = PicoC [PRINT (MinorEqual (Const 2) (Const 3))]

d10 = PicoC [PRINT (Equals (Const 1) (Const 2))]

d11 = PicoC [PRINT (Dif (Const 1) (Const 2))]

d12 = PicoC [PRINT (Not (Const 2))]

d13 = PicoC [PRINT (And (Const 1) (Const 2))]

d14 = PicoC [PRINT (Or (Const 1) (Const 2))]

d15 = PicoC [PRINT (Var "ola")]

-- x = 0;
-- y = 5;
-- while(x < 20){
--    print(Var \"x\");
--    if(x >= 10)
--        then{
--            print(Str \"Entrou na condicaoo if(x >= 10)\");
--            y = (y) * (2);
--            if(y == 20)
--                then{
--                    print(Str \"Entrou na condicao if(y == 20)\");
--                    result = (result) + (1);
--                }
--            else{
--                print(Str \"Entrou na condicao else(y != 20)\");
--                result = (result) + (z);
--            }
--        }
--    else{
--        print(Str \"Entrou na condicao else(x < 10)\");
--        y = (y) - (1);
--        while(y < 0){
--            print(Str \"Entrou no loop interno\");
--            y = (y) + (2);
--            print(Var \"y\");
--        }
--    }
--    x = (x) + (1);
--    %Comentario de teste%
-- }
-- print(Str \"Final do programa\");
-- print(Var \"result\");
--

-- PicoC [
--       Atrib "x" (Const 5),
--       While (Minor (Var "x") (Const 10)) [
--           Atrib "x" (Add (Var "x") (Const 1)),
--           PRINT "Incrementando x",
--           [Atrib "result" (Add (Var "result") (Const 1))],
--           [Atrib "result" (Add (Var "result") (Var "r"))]
--       ],
--       PRINT "Saindo do loop",
--       PRINT "Fim do programa"
--     ]