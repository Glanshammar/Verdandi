#include "mainwindow.h"
#include "./ui_mainwindow.h"
#include <QDebug>
#include <QLibrary>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
{
    ui->setupUi(this);
    loadAllPlugins("Plugins");
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::loadAllPlugins(const QString &pluginDir)
{
    QDir dir(pluginDir);
    if (!dir.exists()) {
        qDebug() << "Plugin directory does not exist:" << pluginDir;
        return;
    }

    QStringList filters;
#ifdef Q_OS_WIN
    filters << "*.dll";
#elif defined(Q_OS_MAC)
    filters << "*.dylib" << "*.so";
#elif
    filters << "*.so";
#endif

    for (const QString &file : dir.entryList(filters, QDir::Files)) {
        QString pluginPath = dir.absoluteFilePath(file);
        qDebug() << "Loading plugin:" << pluginPath;

        QPluginLoader loader(pluginPath);
        QObject *plugin = loader.instance();

        if (plugin) {
            QObject::connect(plugin, SIGNAL(statusMessage(QString)),
                             this, SLOT(onPluginMessage(QString)));

            QMetaObject::invokeMethod(plugin, "initialize",
                                      Q_ARG(QObject*, this));
            qDebug() << "Plugin loaded successfully:" << file;
        } else {
            qDebug() << "Failed to load plugin:" << file
                     << "Error:" << loader.errorString();
        }
    }
}

void MainWindow::on_actionExit_triggered()
{
    QApplication::quit();
}

void MainWindow::onPluginMessage(const QString &msg)
{
    qDebug() << "Plugin message:" << msg;
    statusBar()->showMessage(msg, 3000);
}
