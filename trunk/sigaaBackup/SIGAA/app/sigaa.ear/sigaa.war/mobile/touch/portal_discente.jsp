 	<h:form id="form-portal-discente" styleClass="ui-body ui-body-b ui-corner-all">

		<fieldset>
			<div data-role="fieldcontain">
				<small>
					<c:if test="${usuario.idFoto != null}">
						<img class="fotoPerfil" src="/sigaa/verFoto?idFoto=${usuario.idFoto}&key=${ sf:generateArquivoKey(usuario.idFoto) }" width="50" height="62" style="float: left; padding-right: 6px;"/>
					</c:if>
					<c:if test="${usuario.idFoto == null}">
						<img  class="fotoPerfil" src="${ctx}/img/no_picture.png" width="50" height="62" style="float: left; padding-right: 6px;" />
					</c:if>						
					<h:outputText id="matricula" value="#{ usuario.discente.matricula }"/>
					<c:if test="${ fn:length(acesso.usuario.vinculosPrioritarios) > 1}">
						(<a rel="external" style="font-size: 12px; text-decoration: none;" href="/sigaa/mobile/touch/vinculos.jsf">Alterar Vínculo</a>)
					</c:if>
					<br/>
					<strong><h:outputText value="#{ usuario.nome }"/></strong><br/>
					<h:outputText value="#{ usuario.discente.curso }"/><br/>
				</small>												
			</div>
		</fieldset>
		
		<%@include file="/mobile/touch/include/mensagens.jsp"%>
			
	    <ul data-role="listview" data-inset="true">
	      <li><h:commandLink value="Minhas Turmas" action="#{ portalDiscenteTouch.listarMinhasTurmas }" id="lnkMinhasTurmas" /></li>
          <li><h:commandLink value="Minhas Notas" action="#{ relatorioNotasAluno.gerarRelatorio }" id="lnkMinhasNotas" target="_blank"/></li>
          <li><h:commandLink value="Atestado de Matrícula" action="#{ portalDiscenteTouch.gerarAtestadoMatricula }" id="lnkAtestadoMatricula" target="_blank"/></li>
          <li><h:commandLink value="Consultar Histórico" action="#{ portalDiscenteTouch.gerarHistorico }" id="lnkConsultarHistorico" target="_blank"/></li>
	      <li><h:commandLink value="Biblioteca" action="#{ portalDiscenteTouch.forwardBiblioteca }" id="lnkBiblioteca"/></li>
	      <li><h:commandLink value="Sair" action="#{ loginMobileTouch.logoff }" id="lnkLogOff" onclick="if (!confirm('Tem certeza que deseja sair do sistema?')) return false;"/></li>
	    </ul>
	    
	</h:form>
