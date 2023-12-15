% (P)lot a sine wave from 0 to 2pi

figure()
x = linspace(0,2*pi,100);
y = sin(x);
scatter(x,y,"magenta")
xlabel('x')
ylabel('sin(x)')
title('Plot of Sine Function')


% (S)alt this graph

figure()
noise_level = 0.5;
salty_y = y + noise_level * randn(size(y));
scatter(x, salty_y)

figure()
title('Salted Sine Wave')

hold on

% (S)mooth the salted graph

smoothed_y = smoothdata(salty_y);
scatter(x, salty_y)
scatter(x, smoothed_y,"blue")
xlabel('x')
ylabel('y')
title('Smoothed over salted values')

