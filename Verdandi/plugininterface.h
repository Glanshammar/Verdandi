#ifndef PLUGININTERFACE_H
#define PLUGININTERFACE_H

#include <QObject>
#include <QString>
#include <QPushButton>
#include <QLabel>

class PluginInterface {
public:
    virtual ~PluginInterface() = default;

    virtual QString pluginName() const = 0;
    virtual void initialize() = 0;
    virtual void onButtonClicked(QPushButton* button);
    virtual void updateLabel(QLabel* label);
};

#define PluginInterfaceIID "com.example.PluginInterface"

Q_DECLARE_INTERFACE(PluginInterface, PluginInterfaceIID)

#endif // PLUGININTERFACE_H
