#include "mainwindow.h"
#include "./ui_mainwindow.h"
#include <QDebug>
#include <QLibrary>
#include <QMessageBox>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    setupConnections();
    loadPlugins();
}

MainWindow::~MainWindow()
{
    for (QPluginLoader* loader : pluginLoaders) {
        loader->unload();
        delete loader;
    }

    delete ui;
}

void MainWindow::setupConnections()
{
    // connect(ui->buttonTest, &QPushButton::clicked,
    //        this, &MainWindow::onButtonClicked);
    qDebug() << "No connections setup.";
}

void MainWindow::onButtonClicked()
{
    // Call onButtonClicked on all loaded plugins
    for (PluginInterface* plugin : loadedPlugins) {
        // plugin->onButtonClicked(ui->buttonTest);
    }
}

void MainWindow::loadPlugins()
{
    QDir pluginsDir(qApp->applicationDirPath());

#if defined(Q_OS_MAC)
    pluginsDir.cdUp();
    pluginsDir.cd("PlugIns");
#elif defined(Q_OS_LINUX)
    pluginsDir.cd("plugins");
#elif defined(Q_OS_WIN)
    pluginsDir.cd("plugins");
#endif

    qDebug() << "Looking for plugins in:" << pluginsDir.absolutePath();

    foreach (QString fileName, pluginsDir.entryList(QDir::Files)) {
#if defined(Q_OS_WIN)
        if (!fileName.endsWith(".dll")) continue;
#elif defined(Q_OS_MAC)
        if (!fileName.endsWith(".dylib") && !fileName.endsWith(".so")) continue;
#elif defined(Q_OS_LINUX)
        if (!fileName.endsWith(".so")) continue;
#endif

        QPluginLoader* loader = new QPluginLoader(pluginsDir.absoluteFilePath(fileName));
        QObject* plugin = loader->instance();

        if (plugin) {
            PluginInterface* pluginInterface = qobject_cast<PluginInterface*>(plugin);
            if (pluginInterface) {
                pluginLoaders.append(loader);
                loadedPlugins.append(pluginInterface);
                qDebug() << "Loaded plugin:" << pluginInterface->pluginName()
                         << "from:" << fileName;
            } else {
                qDebug() << "Plugin doesn't implement PluginInterface:" << fileName;
                delete loader;
            }
        } else {
            qDebug() << "Failed to load plugin:" << fileName
                     << "Error:" << loader->errorString();
            delete loader;
        }
    }

    if (loadedPlugins.isEmpty()) {
        qDebug() << "No plugins loaded!";
        qDebug() << "Make sure the plugin is built and copied to the plugins directory.";
    }
}

void MainWindow::on_actionExit_triggered()
{
    QApplication::quit();
}
