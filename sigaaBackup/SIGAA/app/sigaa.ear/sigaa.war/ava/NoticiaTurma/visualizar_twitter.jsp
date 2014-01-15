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

	${ twitterMBean.reiniciarTwitter }

	<h:form id="formTwitter">

		<h:messages showDetail="true" />
		
		<table id='descricaoTwitter'>
			<tr>
				<td style="width:250px;padding-right:15px;vertical-align:middle;">
					<p>Prezado(a) Discente,</p>
					<p>Agora � poss�vel receber as notifica��es de a��es na Turma Virtual pela rede social Twitter (www.twitter.com).</p> 
					<p>Nesta integra��o, as atividades que forem registradas na turma virtual ser�o postadas para o twitter definido pelo docente, e assim os seus seguidores poder�o ser avisados rapidamente.</p>
					<p>Esta � mais uma ferramenta para melhorar a comunica��o professor-aluno atrav�s do SIGAA.</p>
					
					<a4j:outputPanel rendered="#{twitterMBean.usuarioTwitter == null}">
						<p>O docente desta turma ainda n�o definiu uma conta do twitter para esta turma.</p>
					</a4j:outputPanel>
					
					<a4j:outputPanel rendered="#{twitterMBean.usuarioTwitter != null}">
		
						<div class='descricaoOperacao'>O usu�rio do Twitter associado a esta turma � o <strong style="font-weight:bold;">${twitterMBean.usuarioTwitter}</strong>.</div>
						
						<div style="text-align:center;">
							<a href="https://twitter.com/${twitterMBean.usuarioTwitter}" class="twitter-follow-button" data-lang="pt">Siga @${twitterMBean.usuarioTwitter}</a>
							<script src="http://platform.twitter.com/widgets.js" type="text/javascript"></script>
						</div>
						
					</a4j:outputPanel>
					
				</td>
				
				<td>
					<h:graphicImage value="/ava/img/diagrama_twitter.jpg" />
				</td>
			</tr>
		</table>
	</h:form>

</f:view>

<%@include file="/ava/rodape.jsp" %>