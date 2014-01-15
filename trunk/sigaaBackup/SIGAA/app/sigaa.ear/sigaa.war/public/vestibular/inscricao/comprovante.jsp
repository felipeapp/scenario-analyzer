<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
.descricaoOperacao {
	font-size: 1.2em;
}

h3,h4 {
	font-variant: small-caps;
	text-align: center;
	margin: 2px 0 20px;
}

h4 {
	margin: 15px 0 20px;
}

.descricaoOperacao p {
	text-align: justify;
}
th.rotulo{ text-align: right; font-weight: bold; }
</style>

<f:view>
	<h:form id="form">
		<h2>Comprovante de Inscri��o em Processo Seletivo - ${inscricaoVestibular.obj.processoSeletivo.nome}</h2>
	
		<div class="descricaoOperacao">
			<h3>Inscri��o No. ${inscricaoVestibular.obj.numeroInscricao}</h3>
		
			<p>A inscri��o de ${inscricaoVestibular.obj.pessoa.nome}, CPF <ufrn:format
				type="cpf_cnpj" name="inscricaoVestibular" property="obj.pessoa.cpf_cnpj" />,
			foi realizada com sucesso para o
			${inscricaoVestibular.obj.processoSeletivo.nome}, em <i><ufrn:format
				type="dataHora" name="inscricaoVestibular"
				property="obj.dataInscricao" /></i>.</p>

		</div>
			
			<h3 class="tituloTabelaRelatorio">
				Dados da Inscri��o
			</h3>

			
			<table width="100%" class="subFormulario" border="1">
				<tr>
					<th><b>CPF:</b></th>
					<td><ufrn:format type="cpf_cnpj"
						valor="${inscricaoVestibular.obj.pessoa.cpf_cnpj}"></ufrn:format></td>
				</tr>
				<c:if test="${not empty inscricaoVestibular.obj.pessoa.passaporte}">
					<tr>
						<th><b>Passaporte:</b></th>
						<td><h:outputText
							value="#{inscricaoVestibular.obj.pessoa.passaporte}" /></td>
					</tr>
				</c:if>
				<tr>
					<th><b>Nome:</b></th>
					<td><h:outputText
						value="#{inscricaoVestibular.obj.pessoa.nome}" /></td>
				</tr>
				<tr>
					<th><b>�rea de Conhecimento:</b></th>
					<td><h:outputText
						value="#{inscricaoVestibular.obj.opcoesCurso[0].curso.areaVestibular.descricao}" /></td>
				</tr>
				<tr>
					<th><b>Primeira Op��o:</b></th>
					<td><h:outputText
						value="#{inscricaoVestibular.obj.opcoesCurso[0].curso.municipio}" />
					- <h:outputText
						value="#{inscricaoVestibular.obj.opcoesCurso[0]}" /></td>
				</tr>
				<tr>
					<th><b>Segunda Op��o:</b></th>
					<td><h:outputText
						value="#{inscricaoVestibular.obj.opcoesCurso[1].curso.municipio}" />
					- <h:outputText
						value="#{inscricaoVestibular.obj.opcoesCurso[1]}" /></td>
				</tr>
				<tr>
					<th><b>L�ngua Estrangeira:</b></th>
					<td><h:outputText
						value="#{inscricaoVestibular.obj.linguaEstrangeira.denominacao}" /></td>
				</tr>
				<tr>
					<th><b>Regi�o Preferencial de Prova:</b></th>
					<td><h:outputText
						value="#{inscricaoVestibular.obj.regiaoPreferencialProva.denominacao}" /></td>
				</tr>
				<c:if test="${inscricaoVestibular.obj.processoSeletivo.opcaoBeneficioInclusao}">
					<tr>
						<th><b>Argumento de Inclus�o:</b></th>
						<td>
							<h:outputText value="SIM. O candidato concorrer� COM o benef�cio do Argumento de Inclus�o." rendered="#{inscricaoVestibular.obj.optouBeneficioInclusao}" />
							<h:outputText value="N�O. O candidato concorrer� SEM o benef�cio do Argumento de Inclus�o." rendered="#{!inscricaoVestibular.obj.optouBeneficioInclusao}" />
						</td>
					</tr>
				</c:if>
				<tr>
					<th><b>Taxa de Inscri��o:</b></th>
					<td><ufrn:format type="moeda"
						valor="${inscricaoVestibular.obj.valorInscricao}" /></td>
				</tr>
				<tr>
					<th valign="top"><b>Situa��o da Inscri��o:</b></th>
					<td>
						<c:choose>
							<c:when test="${inscricaoVestibular.obj.validada and inscricaoVestibular.obj.valorInscricao == 0 && inscricaoVestibular.obj.processoSeletivo.inscricoesCandidatoAbertas}">
								Pr�-Validada.<br/>
								Em caso de nova inscri��o, esta ser� invalidada e a nova tornar-se-� validada. Consulte a a valida��o definitiva desta inscri��o a partir do dia
								<ufrn:format type="data" valor="${inscricaoVestibular.dataValidacaoIsento}" /> 
							</c:when>
							<c:when test="${inscricaoVestibular.obj.validada and (inscricaoVestibular.obj.valorInscricao > 0 or inscricaoVestibular.obj.valorInscricao == 0 && not inscricaoVestibular.obj.processoSeletivo.inscricoesCandidatoAbertas)}">
								Validada
							</c:when>
							<c:when test="${not inscricaoVestibular.obj.validada}">
								Aguardando confirma��o da COMPERVE
							</c:when>
						</c:choose>
					</td>
				</tr>
			</table>
			<c:if test="${inscricaoVestibular.permiteImpressaoGRU}">
				<br/>
				<p>
					<h:commandLink action="#{acompanhamentoVestibular.imprimirGRU}" style="border: 0;" id="imprimirGRU" styleClass="naoImprimir">
						<h:graphicImage url="/img/imprimir.gif" alt="Imprimir a GRU."/>
						<f:param name="id" value="#{inscricaoVestibular.obj.id}"/>
						<f:param name="inscricao" value="#{inscricaoVestibular.obj.numeroInscricao}"/>
						Imprimir a Guia de Recolhimento da Uni�o (GRU) para pagamento da taxa de inscri��o.
					</h:commandLink>
				</p>
			</c:if>
			<c:if test="${inscricaoVestibular.obj.optouBeneficioInclusao}">
				<br/>
				<p>
					<h:commandLink action="#{inscricaoVestibular.imprimirTermoResponsabilidadeBeneficio}" style="border: 0;" id="imprimirTermoResponsabilidade" styleClass="naoImprimir">
						<h:graphicImage url="/img/imprimir.gif" alt="Imprimir Termo de Responsabilidade do Argumento de Inclus�o."/>
						<f:param name="id" value="#{inscricaoVestibular.obj.id}"/>
						<f:param name="inscricao" value="#{inscricaoVestibular.obj.numeroInscricao}"/>
						Imprimir o Requerimento de Solicita��o do Benef�cio do Argumento de Inclus�o, conforme 
						<h:outputText value="#{ inscricaoVestibular.obj.processoSeletivo.itemEditalArgumentoInclusao } do " 
							rendered="#{ not empty inscricaoVestibular.obj.processoSeletivo.itemEditalArgumentoInclusao }" />
						Edital.
					</h:commandLink>
				</p>
			</c:if>
			<br/><br/><br/>
			<table width="100%">
				<tr>
					<td width="50%" style="text-align: !left;">
						<h:commandLink value="Sair da minha �rea Pessoal" action="#{acompanhamentoVestibular.logoff}" id="logOff" style="font-weight: bold;" styleClass="naoImprimir"/>
					</td>
					<td width="50%" >
						<div align="right">
						<h:commandLink value="<< Voltar � minha �rea Pessoal" action="#{acompanhamentoVestibular.paginaAcompanhamento}" id="paginaAcompanhamento" style="font-weight: bold;" styleClass="naoImprimir" />
						</div>
					</td>
				</tr>
			</table>
		
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>