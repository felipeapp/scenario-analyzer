<script>
alert('Sua sess�o foi expirada. � necess�rio autenticar-se novamente!');
document.location.href = '<%= request.getContextPath() %>?modo=classico';
</script>

