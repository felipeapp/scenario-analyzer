<script>
alert('Sua sessão foi expirada. É necessário autenticar-se novamente!');
document.location.href = '<%= request.getContextPath() %>?modo=classico';
</script>

