cd ..
cd ZPI-UI
npm run build
rm -r ../ZPI-service/src/main/resources/static
mkdir ../ZPI-service/src/main/resources/static
cp -a ./build/. ../ZPI-service/src/main/resources/static/
