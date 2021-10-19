mkdir -p src/main/resources/static
rm -r src/main/resources/static/*
cd ../ZPI-authorize-UI
npm install
npm run build
cp -a ./build/. ../ZPI-authorize-service/src/main/resources/static/