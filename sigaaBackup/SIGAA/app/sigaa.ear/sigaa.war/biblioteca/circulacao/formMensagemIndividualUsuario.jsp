<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

	<h2> <ufrn:subSistema /> &gt; Mensagem para o Usuário: ${enviaMensagemUsuariosBibliotecaMBean.usuarioSelecionado.login} </h2>

	<a4j:keepAlive beanName="enviaMensagemUsuariosBibliotecaMBean" />
	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	
	
	<script type="text/javascript">
	
		window.onload = function() {
			mensagem.show(<%=""+br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM%>, null, '${enviaMensagemUsuariosBibliotecaMBean.usuarioSelecionado.login}' );
		}
		
	</script>


	<h:form>
		<div style="width: 100%; text-align: center;margin-top: 50px; margin-bottom: 50px;">
			
			 <a href="javascript://nop/" onclick="mensagem.show(<%=""+br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM%>, null, '${enviaMensagemUsuariosBibliotecaMBean.usuarioSelecionado.login}' )">
			 Click Aqui para Mostrar o Formulário de Envio de Imagem (Usuários do Internet Explorer)
			 </a>
			
			<br/><br/><br/>
			
			<h:commandLink action="#{enviaMensagemUsuariosBibliotecaMBean.voltaTelaBusca}" value="<< Voltar" />
		</div>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>