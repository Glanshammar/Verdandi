#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QPluginLoader>
#include <QDir>
#include <QStatusBar>
#include "plugininterface.h"

QT_BEGIN_NAMESPACE
namespace Ui {
class MainWindow;
}
QT_END_NAMESPACE

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);
    ~MainWindow();

private slots:
    void loadAllPlugins(const QString &pluginDir);
    void on_actionExit_triggered();
    void onPluginMessage(const QString &msg);
    void onButtonClicked();

private:
    Ui::MainWindow *ui;
    void loadPlugins();
    void setupConnections();
    QList<QPluginLoader*> pluginLoaders;
    QList<PluginInterface*> loadedPlugins;
};
#endif // MAINWINDOW_H
