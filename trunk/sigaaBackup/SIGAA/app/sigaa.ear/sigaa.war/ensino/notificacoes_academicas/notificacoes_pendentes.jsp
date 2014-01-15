<%@include file="/ensino/notificacoes_academicas/cabecalho.jsp"%>

<h2> Notificações Acadêmicas </h2>

<jwr:style src="/css/ensino/notificacoes.css" media="all" />
<a4j:keepAlive beanName="notificacaoAcademicaDiscente"></a4j:keepAlive>
<style>
div.logon h3{ font-size: 12px !important;padding-right:15px;}
.confirmaSenha { float: left !important; }
</style>

<f:view>
	<h:form>

	<img src="${ctx}/img/notificacao/imagemNotificacoes.png" style="padding-left:20px;float:left"/>
	
	<div class="intro">
		<div class="textos">
			${ notificacaoAcademicaDiscente.ultimaNotificacao.notificacaoAcademica.mensagemNotificacao }
		</div>

		<c:if test="${not empty notificacaoAcademicaDiscente.registroNotificacoes}">	
			<div class="textos">
			
				<img src="${ctx}/img/warning.gif" style="vertical-align:middle;"/>
				<b>Caro discente, você já visualizou essa notificação nas seguintes datas:</b><br/> 
				<div style="margin-left:30%">
				<ul>
					<c:forEach items="#{ notificacaoAcademicaDiscente.registroNotificacoes }" var="r" varStatus="loop">
						<li>
						<h:outputText id="data" value="#{r.dataVisualizacao}">
							<f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss"/>
						</h:outputText>	
						</li>
					</c:forEach>
				</ul>
				</div>
			</div>
		</c:if>
		<c:if test="${notificacaoAcademicaDiscente.obj.notificacaoAcademica.exigeConfirmacao}">	
		<div>
			<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
		</div>	
			<div class="botao left" style="margin: 0 auto 40px 5px;clear:both">
				<h:commandLink id="btnResponderQuestionario" 
				action="#{notificacaoAcademicaDiscente.confirmar}">    
					<span>Confirmar Leitura</span>
				</h:commandLink>
			</div>
		</c:if>	
		<c:if test="${!notificacaoAcademicaDiscente.obj.notificacaoAcademica.exigeConfirmacao}">	
			<div class="botao left">
				<h:commandLink id="btnResponderQuestionario" 
				action="#{notificacaoAcademicaDiscente.confirmar}">    
					<span>Confirmar Leitura</span>
				</h:commandLink>
			</div>
			<div class="botao_depois left">
				<h:commandLink id="btnNaoResponderContinuar" 
				action="#{ notificacaoAcademicaDiscente.entrarSemLer }">  
           			<span>Não ler e continuar</span>
				</h:commandLink> 
			</div>
		</c:if>	
			<br clear="all"/>
	</div>
	<c:if test="${notificacaoAcademicaDiscente.obj.notificacaoAcademica.exigeConfirmacao}">	
		<div align="center">
			<span class="required">&nbsp;</span>
			Campos de Preenchimento Obrigatório
		</div>
	</c:if>	
	</h:form>
</f:view>	

<%@include file="/ensino/notificacoes_academicas/rodape.jsp"%>