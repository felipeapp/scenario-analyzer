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
					<p>Agora é possível conectar a sua Turma Virtual a rede social Twitter (www.twitter.com).</p> 
					<p>Nesta integração, as atividades que forem registradas na turma virtual serão postadas para um twitter de sua preferência e assim os seus seguidores poderão ser avisados rapidamente.</p>
					<p>A escolha de qual conta no Twitter conectar a sua turma virtual é sua.  Quando clicar no botão Conectar SIGAA ao Twitter será aberto uma janela informando o login/senha da sua conta no Twitter para realizar a conexão.</p>  
					<p>É mais uma ferramenta para melhorar a comunicação professor-aluno através do SIGAA.</p>
					
					<a4j:outputPanel rendered="#{twitterMBean.usuarioTwitter == null}">
						<p>
							<p:commandButton id="btwitter" style="padding:10px!important;font-size:10pt;" image="logotwitter" ajax="false" action="#{twitterMBean.acessarEnderecoAutenticacao}" value="Conectar Turma ao Twitter" />
						</p>
					</a4j:outputPanel>
					
					<a4j:outputPanel rendered="#{twitterMBean.usuarioTwitter != null}">
		
						<div class='descricaoOperacao'>O usuário do Twitter <strong style="font-weight:bold;">${twitterMBean.usuarioTwitter}</strong> está associado a esta turma. As mensagens referentes às atividades desta turma serão enviadas por ele. Caso queira desassociá-lo, <h:commandLink action="#{twitterMBean.removerTurmaTwitter}" value="clique aqui"/>.</div>
						
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
					Campos de Preenchimento Obrigatório.
				</div>
			</div>
		</a4j:outputPanel>

	</h:form>

</f:view>

<%@include file="/ava/rodape.jsp" %>


