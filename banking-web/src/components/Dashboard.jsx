import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { 
  Building2, 
  LogOut, 
  Plus, 
  ArrowDownCircle, 
  ArrowUpCircle, 
  ArrowRightLeft,
  TrendingUp,
  History,
  Wallet,
  PiggyBank
} from 'lucide-react';

export default function Dashboard({ user, onLogout }) {
  const [accounts, setAccounts] = useState([]);
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  
  // Form states
  const [accountType, setAccountType] = useState('CHECKING');
  const [interestStrategy, setInterestStrategy] = useState('SIMPLE');
  const [amount, setAmount] = useState('');
  const [targetAccountId, setTargetAccountId] = useState('');

  useEffect(() => {
    loadAccounts();
  }, []);

  useEffect(() => {
    if (selectedAccount) {
      loadTransactions(selectedAccount.id);
    }
  }, [selectedAccount]);

  const loadAccounts = async () => {
    try {
      const response = await fetch(`/api/accounts?user_id=${user.id}`);
      const data = await response.json();
      setAccounts(data.accounts || []);
      if (data.accounts && data.accounts.length > 0 && !selectedAccount) {
        setSelectedAccount(data.accounts[0]);
      }
    } catch (err) {
      setError('Erro ao carregar contas');
    }
  };

  const loadTransactions = async (accountId) => {
    try {
      const response = await fetch(`/api/accounts/${accountId}/transactions`);
      const data = await response.json();
      setTransactions(data.transactions || []);
    } catch (err) {
      setError('Erro ao carregar transações');
    }
  };

  const createAccount = async () => {
    setError('');
    setSuccess('');
    setLoading(true);
    
    try {
      const response = await fetch('/api/accounts', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          user_id: user.id,
          account_type: accountType,
          interest_strategy: accountType === 'SAVINGS' ? interestStrategy : null
        })
      });
      
      const data = await response.json();
      
      if (!response.ok) throw new Error(data.error);
      
      setSuccess('Conta criada com sucesso!');
      loadAccounts();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const deposit = async () => {
    if (!selectedAccount || !amount) return;
    
    setError('');
    setSuccess('');
    setLoading(true);
    
    try {
      const response = await fetch(`/api/accounts/${selectedAccount.id}/deposit`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ amount: parseFloat(amount) })
      });
      
      const data = await response.json();
      
      if (!response.ok) throw new Error(data.error);
      
      setSuccess(`Depósito de R$ ${amount} realizado com sucesso!`);
      setAmount('');
      loadAccounts();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const withdraw = async () => {
    if (!selectedAccount || !amount) return;
    
    setError('');
    setSuccess('');
    setLoading(true);
    
    try {
      const response = await fetch(`/api/accounts/${selectedAccount.id}/withdraw`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ amount: parseFloat(amount) })
      });
      
      const data = await response.json();
      
      if (!response.ok) throw new Error(data.error);
      
      setSuccess(`Saque de R$ ${amount} realizado com sucesso!`);
      setAmount('');
      loadAccounts();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const transfer = async () => {
    if (!selectedAccount || !amount || !targetAccountId) return;
    
    setError('');
    setSuccess('');
    setLoading(true);
    
    try {
      const response = await fetch(`/api/accounts/${selectedAccount.id}/transfer`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
          amount: parseFloat(amount),
          target_account_id: targetAccountId
        })
      });
      
      const data = await response.json();
      
      if (!response.ok) throw new Error(data.error);
      
      setSuccess(`Transferência de R$ ${amount} realizada com sucesso!`);
      setAmount('');
      setTargetAccountId('');
      loadAccounts();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const calculateInterest = async () => {
    if (!selectedAccount) return;
    
    setError('');
    setSuccess('');
    setLoading(true);
    
    try {
      const response = await fetch(`/api/accounts/${selectedAccount.id}/calculate-interest`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
      });
      
      const data = await response.json();
      
      if (!response.ok) throw new Error(data.error);
      
      setSuccess(`Juros de R$ ${data.interest.toFixed(2)} calculados com sucesso!`);
      loadAccounts();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
      {/* Header */}
      <header className="bg-white/80 backdrop-blur border-b sticky top-0 z-10">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-gradient-to-br from-blue-600 to-purple-600 rounded-xl flex items-center justify-center">
              <Building2 className="w-6 h-6 text-white" />
            </div>
            <div>
              <h1 className="text-xl font-bold">Sistema Bancário</h1>
              <p className="text-sm text-muted-foreground">Bem-vindo, {user.name}</p>
            </div>
          </div>
          <Button variant="outline" onClick={onLogout}>
            <LogOut className="w-4 h-4 mr-2" />
            Sair
          </Button>
        </div>
      </header>

      <div className="container mx-auto px-4 py-8">
        {error && (
          <Alert variant="destructive" className="mb-4">
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}
        
        {success && (
          <Alert className="mb-4 border-green-500 bg-green-50">
            <AlertDescription className="text-green-700">{success}</AlertDescription>
          </Alert>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Accounts Overview */}
          <div className="lg:col-span-2 space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Minhas Contas</CardTitle>
                <CardDescription>Gerencie suas contas bancárias</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {accounts.map((account) => (
                    <div
                      key={account.id}
                      onClick={() => setSelectedAccount(account)}
                      className={`p-4 rounded-lg border-2 cursor-pointer transition-all ${
                        selectedAccount?.id === account.id
                          ? 'border-blue-500 bg-blue-50'
                          : 'border-gray-200 hover:border-gray-300'
                      }`}
                    >
                      <div className="flex items-center gap-3 mb-2">
                        {account.account_type === 'SAVINGS' ? (
                          <PiggyBank className="w-5 h-5 text-purple-600" />
                        ) : (
                          <Wallet className="w-5 h-5 text-blue-600" />
                        )}
                        <span className="font-medium">
                          {account.account_type === 'SAVINGS' ? 'Poupança' : 'Corrente'}
                        </span>
                      </div>
                      <p className="text-sm text-muted-foreground mb-1">
                        Conta: {account.account_number}
                      </p>
                      <p className="text-2xl font-bold">
                        R$ {parseFloat(account.balance).toFixed(2)}
                      </p>
                      {account.interest_strategy && (
                        <p className="text-xs text-muted-foreground mt-1">
                          Estratégia: {account.interest_strategy === 'SIMPLE' ? 'Simples (2%)' : 'Alto Rendimento (5%)'}
                        </p>
                      )}
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            {/* Transactions */}
            <Card>
              <CardHeader>
                <div className="flex items-center gap-2">
                  <History className="w-5 h-5" />
                  <CardTitle>Histórico de Transações</CardTitle>
                </div>
                <CardDescription>
                  {selectedAccount ? `Conta: ${selectedAccount.account_number}` : 'Selecione uma conta'}
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-2 max-h-96 overflow-y-auto">
                  {transactions.length === 0 ? (
                    <p className="text-center text-muted-foreground py-8">
                      Nenhuma transação encontrada
                    </p>
                  ) : (
                    transactions.map((transaction) => (
                      <div
                        key={transaction.id}
                        className="flex items-center justify-between p-3 rounded-lg border"
                      >
                        <div className="flex items-center gap-3">
                          {transaction.transaction_type === 'DEPOSIT' && (
                            <ArrowDownCircle className="w-5 h-5 text-green-600" />
                          )}
                          {transaction.transaction_type === 'WITHDRAWAL' && (
                            <ArrowUpCircle className="w-5 h-5 text-red-600" />
                          )}
                          {(transaction.transaction_type === 'TRANSFER_IN' || 
                            transaction.transaction_type === 'TRANSFER_OUT') && (
                            <ArrowRightLeft className="w-5 h-5 text-blue-600" />
                          )}
                          {transaction.transaction_type === 'INTEREST' && (
                            <TrendingUp className="w-5 h-5 text-purple-600" />
                          )}
                          <div>
                            <p className="font-medium text-sm">{transaction.description}</p>
                            <p className="text-xs text-muted-foreground">
                              {new Date(transaction.created_at).toLocaleString('pt-BR')}
                            </p>
                          </div>
                        </div>
                        <p className={`font-bold ${
                          transaction.transaction_type === 'WITHDRAWAL' || 
                          transaction.transaction_type === 'TRANSFER_OUT'
                            ? 'text-red-600'
                            : 'text-green-600'
                        }`}>
                          {transaction.transaction_type === 'WITHDRAWAL' || 
                           transaction.transaction_type === 'TRANSFER_OUT' ? '-' : '+'}
                          R$ {parseFloat(transaction.amount).toFixed(2)}
                        </p>
                      </div>
                    ))
                  )}
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Actions Panel */}
          <div className="space-y-6">
            {/* Create Account */}
            <Card>
              <CardHeader>
                <div className="flex items-center gap-2">
                  <Plus className="w-5 h-5" />
                  <CardTitle>Nova Conta</CardTitle>
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label>Tipo de Conta</Label>
                  <select
                    className="w-full p-2 border rounded-md"
                    value={accountType}
                    onChange={(e) => setAccountType(e.target.value)}
                  >
                    <option value="CHECKING">Corrente</option>
                    <option value="SAVINGS">Poupança</option>
                  </select>
                </div>
                
                {accountType === 'SAVINGS' && (
                  <div className="space-y-2">
                    <Label>Estratégia de Juros</Label>
                    <select
                      className="w-full p-2 border rounded-md"
                      value={interestStrategy}
                      onChange={(e) => setInterestStrategy(e.target.value)}
                    >
                      <option value="SIMPLE">Simples (2%)</option>
                      <option value="HIGH_YIELD">Alto Rendimento (5%)</option>
                    </select>
                  </div>
                )}
                
                <Button onClick={createAccount} disabled={loading} className="w-full">
                  Criar Conta
                </Button>
              </CardContent>
            </Card>

            {/* Operations */}
            {selectedAccount && (
              <>
                <Card>
                  <CardHeader>
                    <CardTitle>Operações</CardTitle>
                    <CardDescription>Conta: {selectedAccount.account_number}</CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="space-y-2">
                      <Label>Valor</Label>
                      <Input
                        type="number"
                        placeholder="0.00"
                        value={amount}
                        onChange={(e) => setAmount(e.target.value)}
                        step="0.01"
                        min="0"
                      />
                    </div>
                    
                    <div className="grid grid-cols-2 gap-2">
                      <Button onClick={deposit} disabled={loading} variant="outline">
                        <ArrowDownCircle className="w-4 h-4 mr-2" />
                        Depositar
                      </Button>
                      <Button onClick={withdraw} disabled={loading} variant="outline">
                        <ArrowUpCircle className="w-4 h-4 mr-2" />
                        Sacar
                      </Button>
                    </div>
                  </CardContent>
                </Card>

                <Card>
                  <CardHeader>
                    <CardTitle>Transferência</CardTitle>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="space-y-2">
                      <Label>Conta Destino</Label>
                      <select
                        className="w-full p-2 border rounded-md"
                        value={targetAccountId}
                        onChange={(e) => setTargetAccountId(e.target.value)}
                      >
                        <option value="">Selecione...</option>
                        {accounts
                          .filter(acc => acc.id !== selectedAccount.id)
                          .map(acc => (
                            <option key={acc.id} value={acc.id}>
                              {acc.account_number} - {acc.account_type === 'SAVINGS' ? 'Poupança' : 'Corrente'}
                            </option>
                          ))}
                      </select>
                    </div>
                    
                    <Button onClick={transfer} disabled={loading} className="w-full">
                      <ArrowRightLeft className="w-4 h-4 mr-2" />
                      Transferir
                    </Button>
                  </CardContent>
                </Card>

                {selectedAccount.account_type === 'SAVINGS' && (
                  <Card>
                    <CardHeader>
                      <CardTitle>Rendimento</CardTitle>
                    </CardHeader>
                    <CardContent>
                      <Button onClick={calculateInterest} disabled={loading} className="w-full">
                        <TrendingUp className="w-4 h-4 mr-2" />
                        Calcular Juros
                      </Button>
                    </CardContent>
                  </Card>
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

