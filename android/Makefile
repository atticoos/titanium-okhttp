MODULE_PATH = ~/Library/Application\ Support/Titanium
MODULE_ZIP = tiokhttp.zip

all:
	ant
	cp dist/com.atticoos.tiokhttp-android-1.1.1.zip $(MODULE_PATH)/$(MODULE_ZIP)
	cd $(MODULE_PATH) && \
	unzip -o $(MODULE_PATH)/$(MODULE_ZIP)
	rm $(MODULE_PATH)/$(MODULE_ZIP)

clean:
	rm -rf build/
	rm -rf bin/
	rm -rf libs/
