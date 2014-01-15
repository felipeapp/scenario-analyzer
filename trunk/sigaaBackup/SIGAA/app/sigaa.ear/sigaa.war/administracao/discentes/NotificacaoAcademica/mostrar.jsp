<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<jwr:style src="/css/ensino/notificacoes.css" media="all" />
<a4j:keepAlive beanName="notificacaoAcademica"></a4j:keepAlive>
<style>
div.logon h3{ font-size: 12px !important;padding-right:15px;}
.confirmaSenha { float: left !important; }
</style>

<f:view>
	<h:form>
	<h2 class="title"><ufrn:subSistema /> > Notifica��o Acad�mica</h2>
	
	<div class="descricaoOperacao">
	<b>Caro usu�rio,</b> 
	<br/><br/>
	Est� � a p�gina que ser� mostrada para o discente quando este realizar o acesso no sistema. 
	<br/><br/>
	Para notifica��es que exigem confirma��o, ele dever� preencher seus dados para confirmar a leitura.
	<br/><br/>
	Para as notifica��es que n�o exigem confirma��o, o discente ter� a op��o de n�o ler. Neste caso, ele ser� notificado no seu pr�ximo acesso, at� confirmar a leitura.
	</div>
	
	<img src="${ctx}/img/notificacao/imagemNotificacoes.png" style="padding-left:20px;float:left"/>
	
	<div class="intro">
		<div class="textos">
			${ notificacaoAcademica.obj.mensagemNotificacao }
		</div>
		
		<c:if test="${notificacaoAcademica.obj.exigeConfirmacao}">	
		<div>
			<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
		</div>	
			<div class="botao left" style="margin: 0 auto 40px 5px;clear:both">
				<h:commandLink id="btnResponderQuestionario">    
					<span>Confirmar Leitura</span>
				</h:commandLink>
			</div>
		</c:if>	
		<c:if test="${!notificacaoAcademica.obj.exigeConfirmacao}">	
			<div class="botao left">
				<h:commandLink id="btnResponderQuestionario">    
					<span>Confirmar Leitura</span>
				</h:commandLink>
			</div>
			<div class="botao_depois left">
				<h:commandLink id="btnNaoResponderContinuar">  
           			<span>N�o ler e continuar</span>
				</h:commandLink> 
			</div>
		</c:if>	
		
			<br clear="all"/>
	</div>
	
	<div class="botoes-show" align="center">
		<input type="hidden" name="id" value="${ notificacaoAcademica.obj.id }"/>	
		<h:commandButton action="#{notificacaoAcademica.atualizar}" value="Editar" /> | 
		<h:commandButton action="#{notificacaoAcademica.iniciar}" value="<< Voltar"/>
	</div>
	</h:form>
</f:view>	

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>