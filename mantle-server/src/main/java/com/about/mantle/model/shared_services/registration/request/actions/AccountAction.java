package com.about.mantle.model.shared_services.registration.request.actions;

public class AccountAction extends Action {
    private Account account;

    public AccountAction(Account account) {
        super("account");
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AccountAction{");
        sb.append("account='").append(account);
        sb.append('}');
        sb.append(super.toString());
        return sb.toString();
    }
}
