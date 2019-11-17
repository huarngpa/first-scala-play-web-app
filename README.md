# Modern Web Development with Scala

Following [Denis Kalinin's book](https://leanpub.com/modern-web-development-with-scala). We use the following technologies: Scala, Play, Akka, Javascript, and React to explore web development using Scala technologies.

# Deployment

For `development` you simply need to do:

```sh
sbt run
```

If you'd like to change the front-end assets (`.js` or `.scss` files), you'll need to run the bundler:

```sh
npm run watch
```

Which will build your front-end assets and watch for changes in these files.

Production deployments should proceed as follow:

```sh
npm run build
sbt clean compile stage
cd target/universal/stage
# ➜  stage git:(master) ✗ tree -L 1
# .
# ├── bin
# ├── conf
# ├── lib
# ├── README.md
# └── share
./bin/scala-web-project
```
