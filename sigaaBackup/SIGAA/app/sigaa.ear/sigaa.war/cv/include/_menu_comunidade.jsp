<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<c:if test="${param.ajaxRequest == null and param.dialog == null and sessionScope.ajaxRequest == null}">
<div id="menuComunidade">
	<h:form>
	<rich:toolBar height="24">
		<rich:toolBarGroup>
			<h:commandLink id="principal" action="#{ comunidadeVirtualMBean.paginaPrincipal }" value="Principal" styleClass="principal"/>
		</rich:toolBarGroup>
		<rich:toolBarGroup>
	 		<h:commandLink id="topicos" action="#{ topicoComunidadeMBean.listar }" value="Tópicos" styleClass="topicos"/> 
		</rich:toolBarGroup>
		<rich:toolBarGroup>
			<h:commandLink id="conteudo" action="#{ conteudoComunidadeMBean.listar }" value="Conteúdo" styleClass="conteudo"/>
		</rich:toolBarGroup>
		<rich:toolBarGroup>
			<h:commandLink id="referencias" action="#{ indicacaoReferenciaComunidadeMBean.listar }" value="Referências" styleClass="referencias"/>
		</rich:toolBarGroup>
	    <rich:toolBarGroup>
	    	<h:commandLink id="participantes" action="#{ membroComunidadeMBean.exibirAllParticipantes }" value="Participantes" styleClass="participantes"/>
	    </rich:toolBarGroup>
	    <rich:toolBarGroup>
		  <h:commandLink id="forum" action="#{ forumMensagemComunidadeMBean.listar }" value="Fórum" styleClass="forum">
	      	<f:param name="id" value="#{ comunidadeVirtualMBean.mural.id }"/> 
	      </h:commandLink>      
	    </rich:toolBarGroup>
	    <rich:toolBarGroup>                       
	      <c:if test="${ not comunidadeVirtualMBean.membro.visitante}">	
	      	<h:commandLink id="chat" action="#{ comunidadeVirtualMBean.createChat }" value="Chat" styleClass="chat" 
	      		onclick="window.open('/shared/EntrarChat?idchat=#{ comunidadeVirtualMBean.comunidade.id }&idusuario=#{ comunidadeVirtualMBean.usuarioLogado.id }&passkey=#{ comunidadeVirtualMBean.chatPassKey }&origem=comunidadeVirtual', 'chat_#{ comunidadeVirtualMBean.comunidade.id }', 'height=485,width=685,location=0,resizable=0');"/>
	      </c:if>
	      <c:if test="${ comunidadeVirtualMBean.membro.visitante}">	
	      	<h:commandLink id="chatDesabilitado"action="#" value="Chat" onclick="alert('Atenção: essa opção está disponível apenas para membros da comunidade.');" styleClass="chat" />
	      </c:if>
	    </rich:toolBarGroup>
	    <rich:toolBarGroup>                       
			<h:commandLink id="noticias" action="#{ noticiaComunidadeMBean.listar }" value="Notícias" styleClass="noticias"/>
	    </rich:toolBarGroup>
	    <rich:toolBarGroup>                       
	    	<c:if test="${ not comunidadeVirtualMBean.membro.visitante}">
		  		<h:commandLink id="arquivos" value="Inserir Arquivo" action="#{ arquivoUsuarioCVMBean.inserirArquivoComunidade }" styleClass="arquivos"/>
			</c:if>
			<c:if test="${ comunidadeVirtualMBean.membro.visitante}">
		  		<h:commandLink id="arquivosDesabilitados" value="Inserir Arquivo" action="#" onclick="alert('Atenção: essa opção está disponível apenas para membros da comunidade.');"  styleClass="arquivos"/>
			</c:if>
	    </rich:toolBarGroup>
	    <rich:toolBarGroup>                       
			<h:commandLink id="enquetes" action="#{ enqueteComunidadeMBean.listar }" value="Enquetes" styleClass="enquetes"/>
	    </rich:toolBarGroup>
	    <rich:toolBarGroup rendered="#{comunidadeVirtualMBean.membro.permitidoModerar }">                       
	    	<h:commandLink id="configuracoes" action="#{ comunidadeVirtualMBean.configuracoesComunidade }" value="Configurações"  styleClass="configuracoes"/>
	    </rich:toolBarGroup>          
	</rich:toolBar>
	</h:form>
</div>
</c:if>