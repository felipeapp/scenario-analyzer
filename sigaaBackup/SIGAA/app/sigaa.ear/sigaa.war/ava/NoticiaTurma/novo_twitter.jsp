<%@include file="/ava/cabecalho.jsp" %>

<style>

	#descricaoTwitter {
		position:relative;
		margin:auto;
	}
	
	#descricaoTwitter p {
		margin-bottom:10px;
	}
	
	
	.logotwitter {
		background:url('/sigaa/ava/img/btwitter.png') no-repeat !important;
		width:32px!important;
		height:40px!important;
		margin:-13px 0px 0px 3px!important;
		padding:0px!important;
	}
	
</style>

<f:view>
	
	<%@include file="/ava/menu.jsp" %>

	${ twitterMBean.verificaErro }
	${ twitterMBean.reiniciarTwitter }
	
	<h:form id="formTwitter">

		<h:messages showDetail="true" />
		
		<table id='descricaoTwitter'>
			<tr>
				<td style="width:250px;padding-right:15px;vertical-align:middle;">
					<p>Prezado(a) Docente,</p>
					<p>Agora � poss�vel conectar a sua Turma Virtual a rede social Twitter (www.twitter.com).</p> 
					<p>Nesta integra��o, as atividades que forem registradas na turma virtual ser�o postadas para um twitter de sua prefer�ncia e assim os seus seguidores poder�o ser avisados rapidamente.</p>
					<p>A escolha de qual conta no Twitter conectar a sua turma virtual � sua.  Quando clicar no bot�o Conectar SIGAA ao Twitter ser� aberto uma janela informando o login/senha da sua conta no Twitter para realizar a conex�o.</p>  
					<p>� mais uma ferramenta para melhorar a comunica��o professor-aluno atrav�s do SIGAA.</p>
					
					<a4j:outputPanel rendered="#{twitterMBean.usuarioTwitter == null}">
						<p>
							<p:commandButton id="btwitter" style="padding:10px!important;font-size:10pt;" image="logotwitter" ajax="false" action="#{twitterMBean.acessarEnderecoAutenticacao}" value="Conectar Turma ao Twitter" />
						</p>
					</a4j:outputPanel>
					
					<a4j:outputPanel rendered="#{twitterMBean.usuarioTwitter != null}">
		
						<div class='descricaoOperacao'>O usu�rio do Twitter <strong style="font-weight:bold;">${twitterMBean.usuarioTwitter}</strong> est� associado a esta turma. As mensagens referentes �s atividades desta turma ser�o enviadas por ele. Caso queira desassoci�-lo, <h:commandLink action="#{twitterMBean.removerTurmaTwitter}" value="clique aqui"/>.</div>
						
						<div class="formulario" style="width:500px;">
							<h2>Enviar nova mensagem pelo Twitter</h2>
							<%@include file="/ava/NoticiaTurma/_form_twitter.jsp" %>
						</div>
					</a4j:outputPanel>
					
				</td>
				
				<td>
					<h:graphicImage value="/ava/img/diagrama_twitter.jpg" />
				</td>
			</tr>
		</table>
			
		<a4j:outputPanel rendered="#{twitterMBean.usuarioTwitter != null}">
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{twitterMBean.postar}" value="Postar" /> 
				</div>
				<div class="required-items">
					<span class="required">&nbsp;</span>
					Campos de Preenchimento Obrigat�rio.
				</div>
			</div>
		</a4j:outputPanel>

	</h:form>

</f:view>

<%@include file="/ava/rodape.jsp" %>


