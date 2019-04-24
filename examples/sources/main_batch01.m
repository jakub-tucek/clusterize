
function [] = main_batch01(minTransLen, maxTransLen, vicinitySize, ...
                               subImgIdx, varargin)
% get value of bash variables
IN_DIR = getenv( 'IN_DIR');
OUT_DIR = getenv('OUT_DIR');

disp('in dir');
disp(IN_DIR);
disp('out dir');
disp(IN_DIR);


% set default values
layers = [25, 25];
useGPU = 'no';

for i = 1 : 2 : length(varargin)
    name = varargin{i};
    value = varargin{i+1};
    switch name
        case 'useGPU'
            useGPU = value;
        case 'layers'
            layers = value;
        otherwise
    end
end


disp('args');
disp(minTransLen);
disp(maxTransLen);
disp(vicinitySize);

disp('useGPU');
disp(useGPU);
disp('layers');
disp(layers);

disp('sleep')
java.lang.Thread.sleep(10*100)