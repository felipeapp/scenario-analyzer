<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2><ufrn:subSistema /> > Assinaturas nos Diplomas</h2>
	<h:form id="form">
	<a4j:keepAlive beanName="responsavelAssinaturaDiplomasBean"></a4j:keepAlive>
	<div class="descricaoOperacao">
		<p><b>Caro usuário,</b></p>
		<p>Este formulário permite definir os nomes e funções dos diretores que assinam o diploma do discente.</p>
	</div>
		<table class="formulario" width="65%">
			<caption>Informe os Nomes e Respectivas Funções</caption>
			<tbody>
				<c:if test="${fn:length(responsavelAssinaturaDiplomasBean.niveisHabilitados) > 1}">
					<tr>
						<th style="text-align: right;" width="130px" class="obrigatorio">Nível de Ensino:</th>
						<td> 
							<h:selectOneMenu value="#{responsavelAssinaturaDiplomasBean.obj.nivel}" id="nivel" onchange="submit()">
								<f:selectItems value="#{responsavelAssinaturaDiplomasBean.niveisHabilitadosCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
				</c:if>
				<c:if test="${responsavelAssinaturaDiplomasBean.obj.id > 0 && !responsavelAssinaturaDiplomasBean.obj.ativo}">
					<tr>
						<th>
							<h:selectBooleanCheckbox value="#{responsavelAssinaturaDiplomasBean.obj.ativo}" id="ativo"/>
						</th>
						<td>Usar estes nomes na geração de diplomas.</td>
					</tr>
				</c:if>
				<tr>
					<td colspan="2" class="subFormulario">Reitor da Instituição</td>
				</tr>
				<tr>
					<th class="required">Nome: </th>
					<td>
						<h:inputText value="#{responsavelAssinaturaDiplomasBean.obj.nomeReitor}" id="nomeReitor" size="70" maxlength="120"/>
					</td>
				</tr>
				<tr>
					<th>Gênero: </th>
					<td>
						<h:selectOneRadio value="#{responsavelAssinaturaDiplomasBean.obj.generoReitor }" id="generoReitor">
							<f:selectItems value="#{ responsavelAssinaturaDiplomasBean.mascFem }"/>
						</h:selectOneRadio>
					</td>
				</tr>
				<tr>
					<th class="required">Descrição da Função: </th>
					<td>
						<h:inputText value="#{responsavelAssinaturaDiplomasBean.obj.descricaoFuncaoReitor}" id="funcaoReitor" size="70" maxlength="120"/>
					</td>
				</tr>
				<c:if test="${ responsavelAssinaturaDiplomasBean.obj.graduacao }">
					<tr>
						<td colspan="2" class="subFormulario">Diretor da Unidade de Registro de Diplomas</td>
					</tr>
					<tr>
						<th class="required">Nome: </th>
						<td>
							<h:inputText value="#{responsavelAssinaturaDiplomasBean.obj.nomeDiretorUnidadeDiplomas}" id="nomeDiretorDred" size="70" maxlength="120"/>
						</td>
					</tr>
					<tr>
						<th>Gênero: </th>
						<td>
							<h:selectOneRadio value="#{responsavelAssinaturaDiplomasBean.obj.generoDiretorUnidadeDiplomas }" id="generoDiretorUnidadeDiplomas">
								<f:selectItems value="#{ responsavelAssinaturaDiplomasBean.mascFem }"/>
							</h:selectOneRadio>
						</td>
					</tr>
					<tr>
						<th class="required">Descrição da Função: </th>
						<td>
							<h:inputText value="#{responsavelAssinaturaDiplomasBean.obj.descricaoFuncaoDiretorUnidadeDiplomas}" id="funcaoDiretorDred" size="70" maxlength="120"/>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="subFormulario">Responsável pela Graduação na Instituição</td>
					</tr>
					<tr>
						<th class="required">Nome:</th>
						<td>
							<h:inputText value="#{responsavelAssinaturaDiplomasBean.obj.nomeDiretorGraduacao}" id="nomeDiretorDae" size="70" maxlength="120"/>
						</td>
					</tr>
					<tr>
						<th>Gênero: </th>
						<td>
							<h:selectOneRadio value="#{responsavelAssinaturaDiplomasBean.obj.generoDiretorGraduacao }" id="generoDiretorGraduacao">
								<f:selectItems value="#{ responsavelAssinaturaDiplomasBean.mascFem }"/>
							</h:selectOneRadio>
						</td>
					</tr>
					<tr>
						<th class="required">Descrição da Função:</th>
						<td>
							<h:inputText value="#{responsavelAssinaturaDiplomasBean.obj.descricaoFuncaoDiretorGraduacao}" id="funcaoDiretorDae" size="70" maxlength="120"/>
						</td>
					</tr>
				</c:if>
				<c:if test="${ !responsavelAssinaturaDiplomasBean.obj.graduacao }">
					<tr>
						<td colspan="2" class="subFormulario">Responsável pela Pós-Graduação na Instituição</td>
					</tr>
					<tr>
						<th class="required">Nome:</th>
						<td>
							<h:inputText value="#{responsavelAssinaturaDiplomasBean.obj.nomeDiretorPosGraduacao}" id="nomeProReitorPosGraduacao" size="70" maxlength="120"/>
						</td>
					</tr>
					<tr>
						<th>Gênero: </th>
						<td>
							<h:selectOneRadio value="#{responsavelAssinaturaDiplomasBean.obj.generoDiretorPosGraduacao }" id="generoDiretorPosGraduacao">
								<f:selectItems value="#{ responsavelAssinaturaDiplomasBean.mascFem }"/>
							</h:selectOneRadio>
						</td>
					</tr>
					<tr>
						<th class="required">Descrição da Função:</th>
						<td>
							<h:inputText value="#{responsavelAssinaturaDiplomasBean.obj.descricaoFuncaoDiretorPosGraduacao}" id="descricaoProReitorPosGraduacao" size="70" maxlength="120"/>
						</td>
					</tr>
				</c:if>
				<c:if test="${ responsavelAssinaturaDiplomasBean.obj.latoSensu }">
					<tr>
						<td colspan="2" class="subFormulario">Responsável pelo Setor de Controle de Certificados Lato Sensu</td>
					</tr>
					<tr>
						<th class="required">Nome:</th>
						<td>
							<h:inputText value="#{responsavelAssinaturaDiplomasBean.obj.nomeResponsavelCertificadosLatoSensu}" id="nomeResponsavelCertificadosLatoSensu" size="70" maxlength="120"/>
						</td>
					</tr>
					<tr>
						<th>Gênero: </th>
						<td>
							<h:selectOneRadio value="#{responsavelAssinaturaDiplomasBean.obj.generoResponsavelCertificadosLatoSensu }" id="generoResponsavelCertificadosLatoSensu">
								<f:selectItems value="#{ responsavelAssinaturaDiplomasBean.mascFem }"/>
							</h:selectOneRadio>
						</td>
					</tr>
					<tr>
						<th class="required">Descrição da Função:</th>
						<td>
							<h:inputText value="#{responsavelAssinaturaDiplomasBean.obj.descricaoFuncaoResponsavelCertificadosLatoSensu}" id="descricaoFuncaoResponsavelCertificadosLatoSensu" size="70" maxlength="120"/>
						</td>
					</tr>
				</c:if>
			</tbody>
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton value="#{ responsavelAssinaturaDiplomasBean.confirmButton }" action="#{ responsavelAssinaturaDiplomasBean.cadastrar }" id="btnCadastrar"/>
					<h:commandButton value="Cancelar" action="#{ responsavelAssinaturaDiplomasBean.cancelar }" onclick="#{ confirm }" id="btnCancelar"/>
				</td></tr>
			</tfoot>
		</table>

		<br />
		<center><img src="/shared/img/required.gif" style="vertical-align: middle;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span></center>
		<br />

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>