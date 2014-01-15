<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Docente Externo</h2>
	<h:outputText value="#{docenteExterno.create}" />

	<table class="visualizacao" >
		<tr>
			<th width="20%">CPF:</th>
			<td colspan="3">
				<ufrn:format type="cpf_cnpj" valor="${docenteExterno.obj.pessoa.cpf_cnpj}">
				</ufrn:format>
			</td>
		</tr>
		<tr>
			<th width="20%">Nome:</th>
			<td colspan="3">${docenteExterno.obj.pessoa.nome}</td>
		</tr>
	</table>
	<br />	
	
	
	<table class="formulario" width="700">
		<h:form id="form">
			<caption class="listagem">Dados do Docente Externo</caption>
			<h:inputHidden value="#{docenteExterno.confirmButton}" id="confirmButton" />
			<h:inputHidden value="#{docenteExterno.obj.id}" id="obj_id" />
			<tbody>
				<!-- formação -->
				<tr>
					<th class="required">Formação:</th>
					<td colspan="3"><h:selectOneMenu value="#{ docenteExterno.obj.formacao.id }"
						id="obj_formacao">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{formacao.allCombo}" />
					</h:selectOneMenu> 
					</td>
				</tr>

				<!-- instituição de ensino -->
				<tr>
					<th class="required">Instituição de Ensino:</th>
					<td colspan="3"><h:selectOneMenu value="#{docenteExterno.obj.instituicao.id}" disabled="#{!docenteExterno.escolheInstituicao}"
						id="obj_pessoa_instituicao">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{instituicoesEnsino.allCombo}" />
					</h:selectOneMenu>
					 </td>
				</tr>

				<tr>
					<th class="required">Tipo:</th>
					<td colspan="3">
						<h:outputText value="#{docenteExterno.obj.tipoDocenteExterno.denominacao}" rendered="#{not docenteExterno.escolheTipo}" />
						<h:selectOneMenu rendered="#{docenteExterno.escolheTipo}" value="#{docenteExterno.obj.tipoDocenteExterno.id}" disabled="#{!docenteExterno.escolheTipoDocenteExterno}" id="selectTipoDocenteExterno">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{tipoDocenteExterno.allCombo}" />
						</h:selectOneMenu> 
					</td>
				</tr>
				
				<tr>
					<th class="required">Válido Até:</th>
					<td>
						<t:inputCalendar value="#{docenteExterno.obj.prazoValidade}" size="10" disabled="#{dadosPessoais.readOnly}" 
							id="dataValidade" maxlength="10" renderAsPopup="true" renderPopupButtonAsImage="true"
							popupDateFormat="dd/MM/yyyy" onkeypress="return(formataData(this, event))" >
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
					 </td>
				</tr>
				
				<c:if test="${docenteExterno.escolheNivelEnsino}">
				<tr>
					<th>Nível de Ensino:</th>
					<td colspan="3">
						<h:selectOneMenu value="#{docenteExterno.obj.nivel}" id="selectNivelDocenteExterno" rendered="#{docenteExterno.escolheNivelEnsino}">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{nivelEnsino.allCombo}" />
						</h:selectOneMenu> 
					</td>
				</tr>
				</c:if>
				
				<tr>
					<th class="required">
						<c:choose>
							<c:when test="${docenteExterno.graduacao}"><b>Departamento:</b></c:when>
							<c:when test="${docenteExterno.stricto}"><b>Programa:</b></c:when>
							<c:when test="${docenteExterno.tecnico and docenteExterno.obj.id != 0}"><b>Escola:</b></c:when>
							<c:when test="${docenteExterno.lato and !docenteExterno.escolheDepartamento}"><b>Departamento ou Programa:</b></c:when>
							<c:when test="${docenteExterno.lato and docenteExterno.escolheDepartamento}">Departamento ou Programa:</c:when>
							<c:when test="${docenteExterno.infantil}"><b>Escola:</b></c:when>
						</c:choose>
					</th>
					<td>
						<c:choose>
							<c:when test="${docenteExterno.infantil}">
								${docenteExterno.unidade.nome}
							</c:when>	
							<c:when test="${docenteExterno.medio}">
								${docenteExterno.obj.unidade.nome}
							</c:when>											
							<c:when test="${docenteExterno.stricto and docenteExterno.escolheDepartamento}">
								<c:if test="${acesso.ppg}">
								<h:selectOneMenu style="width: 380px;" value="#{docenteExterno.obj.unidade.id}" id="departamento">
								<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
									<f:selectItems value="#{unidade.allProgramaPosCombo}" />
								</h:selectOneMenu>
								</c:if>
								<c:if test="${!acesso.ppg && (acesso.secretariaPosGraduacao || acesso.coordenadorCursoStricto)}">
									${docenteExterno.obj.unidade}
								</c:if>
							</c:when>
							<c:when test="${docenteExterno.lato and docenteExterno.escolheDepartamento}">
								<h:selectOneMenu style="width: 380px;" value="#{docenteExterno.obj.unidade.id}" id="departamento">
								<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
									<f:selectItems value="#{unidade.allDeptosProgramasEscolasCombo}" />
								</h:selectOneMenu>
							</c:when>
							<c:when test="${docenteExterno.lato and !docenteExterno.escolheDepartamento}">
									${docenteExterno.obj.unidade.nome}
							</c:when>
							<c:when test="${docenteExterno.graduacao}">
								<c:if test="${docenteExterno.escolheDepartamento}">
									<h:selectOneMenu style="width: 380px;" value="#{docenteExterno.obj.unidade.id}" id="departamento">
									<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
										<f:selectItems value="#{unidade.allDeptosEscolasCombo}" />
									</h:selectOneMenu>
								</c:if>
								<c:if test="${not docenteExterno.escolheDepartamento}">
									${docenteExterno.obj.unidade}
								</c:if>
							</c:when>						
							<c:when test="${docenteExterno.tecnico and docenteExterno.obj.id != 0}">
								${docenteExterno.obj.unidade}
							</c:when>
							<c:when test="${acesso.administradorSistema}">
								<h:selectOneMenu style="width: 380px;" value="#{docenteExterno.obj.unidade.id}" id="departamento">
									<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
									<f:selectItems value="#{unidade.allDeptosProgramasEscolasCombo}" />
								</h:selectOneMenu>
							</c:when>
						</c:choose>
					</td>
				</tr>

			</tbody>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="#{docenteExterno.confirmButton}" action="#{docenteExterno.cadastrar}" id="btncadastrar" />
						<c:choose>
							<c:when test="${docenteExterno.obj.id == 0}">
								<h:commandButton value="<< Novo Cadastro de Pessoa" action="#{docenteExterno.popular}" id="btnpopular"/>
								<h:commandButton value="<< Voltar" action="#{docenteExterno.voltaTelaDadosPessoais}" id="btnvoltarPessoais"/>
							</c:when>
							<c:otherwise>
								<h:commandButton value="<< Voltar" action="#{docenteExterno.voltar}" id="btnvoltar" />
							</c:otherwise>
						</c:choose>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{docenteExterno.cancelar}" id="btncancelar" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br/>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
