# Integ Web for Spring Web MVC Mixer2

Spring Web MVC と Mixer2 を使用した Web アプリケーション用フレームワークです。

## IT テスト

テスト実行時に JaCoCo によるカバレッジを計測します。

```
mvn -P it clean jacoco:prepare-agent test jacoco:report 
```

## リリース手順

次の手順でリリースします。

* リリースバージョンの付与

pom.xml を編集し、<version/> をリリースバージョンに変更します。

* jar ファイルのローカルインストール

リリースバージョンの jar ファイルをローカルリポジトリにインストールします。

```
mvn -Duser.name="Integsystem Corporation" clean install
```

* commit と push

リリースバージョンを commit し、push します。

```
git commit -a -m "Integ Web for Spring Web MVC Mixer2 0.0.x Release."
git push origin master
```

* tag と push

リリースバージョン のtag を作成し、push します。バージョンの前には v を付与します。

```
git tag -a v0.0.x -m "Integ Web for Spring Web MVC Mixer2 0.0.x Release."
git push origin refs/tags/vx.y.z
```

必要に応じて、CI でこのリリースバージョンのテスト実行や CI のリポジトリへのインストールを行います。

* 次の SNAPSHOT バージョンの付与

pom.xml を編集し、<version/> を次の SNAPSHOT バージョンに変更します。

* commit と push

次の SNAPSHOT バージョンを commit し、push します。

```
git commit -a -m "Integ Web for Spring Web MVC Mixer2 0.0.x-SNAPSHOT Start."
git push origin master
```
