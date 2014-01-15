<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Usuário do Docente Externo</h2>
	<h:outputText value="#{usuarioDocenteExterno.create}" />
	<h:messages showDetail="true" />
	<table class="formulario" width="700">
		<h:form id="form">
			<caption class="listagem">Dados do Usuário do Docente Externo</caption>
			<h:inputHidden value="#{usuarioDocenteExterno.confirmButton}" id="confirmButton" />
			<h:inputHidden value="#{usuarioDocenteExterno.obj.id}" id="obj_id" />
			<h:inputHidden value="#{usuarioDocenteExterno.departamento}" id="boodepto"/>
			<h:inputHidden value="#{usuarioDocenteExterno.usuario.unidade.id}" id="idUnidade"/>
			<h:inputHidden value="#{usuarioDocenteExterno.usuario.unidade.nome}" id="nomeUnidade"/>
			<tbody>
				<tr>
					<th class="required">Departamento:</th>
					<c:if test="${ !usuarioDocenteExterno.departamento and usuarioDocenteExterno.usuario.unidade.id == 0}">
					<td colspan="3">
					
						<h:selectOneMenu value="#{ usuarioDocenteExterno.usuario.unidade.id }" id="unidade">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>	
							
							<c:choose>
							
								<c:when test="${acesso.administracao}">
									<f:selectItems value="#{unidade.allCombo}" />
								</c:when>
							
								<c:when test="${acesso.ppg}">
									<f:selectItems value="#{unidade.allProgramaPosCombo}" />
								</c:when>
								<c:when test="${acesso.dae}">
									<f:selectItems value="#{unidade.allDepartamentoCombo}" />
								</c:when>
								<c:when test="${acesso.lato}">
									<f:selectItems value="#{unidade.allDeptosProgramasEscolasCombo}" />
								</c:when>
								
							</c:choose>
							
						</h:selectOneMenu> 
					</td>
					</c:if>
					<c:if test="${ usuarioDocenteExterno.departamento or usuarioDocenteExterno.usuario.unidade.id != 0}">
						<td colspan="3">
							<h:outputText value="#{usuarioDocenteExterno.usuario.unidade}"/>
							<h:inputHidden value="#{usuarioDocenteExterno.usuario.unidade.id}" />
						</td>
					</c:if>
				</tr>			
				<tr>
					<th class="required">E-Mail:</th>
					<td>
						<h:inputText value="#{ usuarioDocenteExterno.usuario.email }"
						size="45" id="txtEmail" />
					</td>
				</tr>			
				<tr>
					<th class="required">Login:</th>
					<td>
						<h:inputText value="#{ usuarioDocenteExterno.usuario.login }" id="txtlogin" size="15" />
					</td>
				</tr>
				<tr>
					<th class="required">Senha:</th>
					<td>
						<h:inputSecret value="#{ usuarioDocenteExterno.usuario.senha }"  id="secretSenha" size="15" />
					</td>
				</tr>
				<tr>
					<th class="required">Confirmar Senha:</th>
					<td>
						<h:inputSecret value="#{ usuarioDocenteExterno.usuario.confirmaSenha }" size="15" id="secretConfirmSenha" />
					</td>
				</tr>

			</tbody>
			<tfoot>
				<tr>
					<td colspan="4">
					 <h:commandButton value="Cadastrar" action="#{usuarioDocenteExterno.cadastrar}" id="btncadastrar" /> 
					 <h:commandButton value="Cancelar" onclick="#{confirm}" action="#{usuarioDocenteExterno.cancelar}" id="btncancelar" />
					</td>
				</tr>
			</tfoot>
		</h:form>
		
	</table>
	<br/>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
