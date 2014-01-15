<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Imprimir Segunda Via da GRU</h2>

	<h:form id="form">
		<table class="listagem" width="95%">
			<caption>Dados da Inscri��o</caption>
			<tbody>
				<tr>
					<td colspan="6" class="subFormulario">Dados Pessoais</td>
				</tr>
				<tr>
					<th class="rotulo" >CPF:</th>
					<td colspan="4">
						<ufrn:format type="cpf_cnpj" valor="${relatoriosVestibular.inscricao.pessoa.cpf_cnpj}"></ufrn:format>
					</td>
					<td rowspan="11" valign="top" style="text-align: right;">
						<c:if test="${not empty relatoriosVestibular.inscricao.pessoa.idFoto}">
							<img src="${ctx}/verFoto?idArquivo=${relatoriosVestibular.inscricao.pessoa.idFoto}&key=${ sf:generateArquivoKey(relatoriosVestibular.inscricao.pessoa.idFoto) }" style="width: 150px; height: 200px"/>
						</c:if>
					</td>
				</tr>
				<tr>
					<th class="rotulo">Nome:</th>
					<td colspan="4"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.nome}" /></td>
				</tr>
				<tr>
					<th class="rotulo">E-Mail:</th>
					<td colspan="4"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.email}" /></td>
				</tr>

				<!-- ============= DOCUMENTA��O  -->
				<tr>
					<td colspan="5" class="subFormulario">Documento de Identidade</td>
				</tr>
				<tr>
					<th class="rotulo">N� do Doc. de Identifica��o:</th>
					<td colspan="2"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.identidade.numero}" /></td>
					<th class="rotulo">�rg�o de Expedi��o:</th>
					<td><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.identidade.orgaoExpedicao}" /></td>
				</tr>
				<tr>
					<th class="rotulo">UF:</th>
					<td colspan="2"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.identidade.unidadeFederativa.descricao}" /></td>
					<th class="rotulo">Data de Expedi��o:</th>
					<td><t:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.identidade.dataExpedicao}" /></td>
				</tr>
			<tr>
				<td colspan="5" class="subFormulario">Inscri��o N� ${relatoriosVestibular.inscricao.numeroInscricao}</td>
			</tr>
			<tr>
				<th colspan="2"><b>�rea de Conhecimento:</b></th>
				<td colspan="3"><h:outputText
					value="#{relatoriosVestibular.inscricao.opcoesCurso[0].curso.areaVestibular.descricao}" /></td>
			</tr>
			<tr>
				<th colspan="2" valign="top"><b>Primeira Op��o:</b></th>
				<td colspan="3"><h:outputText
					value="#{relatoriosVestibular.inscricao.opcoesCurso[0].curso.municipio}" />
				- <h:outputText
					value="#{relatoriosVestibular.inscricao.opcoesCurso[0]}" /></td>
			</tr>
			<tr>
				<th colspan="2" valign="top"><b>Segunda Op��o:</b></th>
				<td colspan="3"><h:outputText
					value="#{relatoriosVestibular.inscricao.opcoesCurso[1].curso.municipio}" />
				- <h:outputText
					value="#{relatoriosVestibular.inscricao.opcoesCurso[1]}" /></td>
			</tr>
			<tr>
				<th colspan="2"><b>L�ngua Estrangeira:</b></th>
				<td colspan="3"><h:outputText
					value="#{relatoriosVestibular.inscricao.linguaEstrangeira.denominacao}" /></td>
			</tr>
			<tr>
				<th colspan="2"><b>Regi�o Preferencial de Prova:</b></th>
				<td colspan="4"><h:outputText
					value="#{relatoriosVestibular.inscricao.regiaoPreferencialProva.denominacao}" /></td>
			</tr>
			<tr>
				<th colspan="2" valign="top"><b>Situa��o da Inscri��o:</b></th>
				<td colspan="4">
					<c:choose>
						<c:when test="${relatoriosVestibular.inscricao.validada and 
											relatoriosVestibular.inscricao.valorInscricao == 0 && 
											relatoriosVestibular.inscricao.processoSeletivo.inscricoesCandidatoAbertas}">
							Pr�-Validada.<br/> Em caso de nova inscri��o, esta ser� invalidada e a nova tornar-se-� validada. Consulte a a valida��o definitiva desta inscri��o a partir do dia
							<ufrn:format type="data" valor="${relatoriosVestibular.inscricao.processoSeletivo.fimInscricaoCandidato}" /> 
						</c:when>
						<c:when test="${relatoriosVestibular.inscricao.validada and (relatoriosVestibular.inscricao.valorInscricao > 0 or relatoriosVestibular.inscricao.valorInscricao == 0 && not relatoriosVestibular.inscricao.processoSeletivo.inscricoesCandidatoAbertas)}">
							Validada
						</c:when>
						<c:when test="${not relatoriosVestibular.inscricao.validada}">
							Aguardando confirma��o da COMPERVE
						</c:when>
					</c:choose>
				</td>
			</tr>
		</table>
		<br/>
		<table class="formulario" width="40%">
			<caption>Dados da GRU</caption>
			<tbody>
				<tr>
					<th class="rotulo">Valor:</th>
					<td><ufrn:format valor="${relatoriosVestibular.inscricao.valorInscricao}" type="moeda"/></td>
				</tr>
				<tr>
					<th class="required">Data de Vencimento:</th>
					<td>
						<t:inputCalendar id="dataVencimento" renderAsPopup="true"
							renderPopupButtonAsImage="true" size="10" maxlength="10"
							onkeypress="return formataData(this,event)"
							disabled="#{readOnly}" popupDateFormat="dd/MM/yyyy"
							value="#{relatoriosVestibular.dataVencimentoGRU}" />  
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton action="#{ relatoriosVestibular.imprimirGRU }" value="Imprimir" id="imprimirGRU"/>
						<h:commandButton value="Cancelar" action="#{relatoriosVestibular.cancelar}" id="cancelar" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<center>
			<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
			<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
		</center>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>