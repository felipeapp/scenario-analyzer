<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
.direita {
	text-align: right;
}

.esquerda {
	text-align: left;
}
</style>

<f:view>
<h2> <ufrn:subSistema /> > Segunda Via de Diplomas </h2>

<h:form id="form">
<a4j:keepAlive beanName="responsavelAssinaturaDiplomasBean"/>
<div class="descricaoOperacao">Para emitir uma segunda via, faz-se
		necessário informar o número do processo.</div>

<table class="visualizacao" >
	<caption>Dados do Registro</caption>
	<tr>
		<th> Livro: </th>
		<td>
			<h:outputText value="#{registro.livroRegistroDiploma.titulo}"/>
		</td>
		<th> Folha: </th>
		<td>
			<h:outputText value="#{registro.folha.numeroFolha}"/>
		</td>
	</tr>
	
	<tr>
		<th>Discente:</th>
		<td>
			<h:outputText value="#{registro.discente.nome}"/>
		</td>
		<th> Número do Registro: </th>
		<td>
			<h:outputText value="#{registro.numeroRegistro}"/>
		</td>
	</tr>
	</tr>
	<tr>
		<th>Pai:</th>
		<td colspan="3">
			<h:outputText value="#{registro.discente.pessoa.nomePai}"/>
		</td>
	</tr>
	<tr>
		<th>Mãe:</th>
		<td colspan="3">
			<h:outputText value="#{registro.discente.pessoa.nomeMae}"/>
		</td>
	</tr>
	<tr>
		<th>Nascido em:</th>
		<td>
			<fmt:formatDate value="${registro.discente.pessoa.dataNascimento}" pattern="dd 'de' MMMMMMM 'de' yyyy"/>
		</td>
		<th>Identidade:</th>
		<td>
			<h:outputText value="#{registro.discente.pessoa.identidade}"/>
		</td>
	</tr>
	<tr>
		<th>Naturalidade:</th>
		<td colspan="3">
			<h:outputText value="#{registro.discente.pessoa.municipio}"/>
			<h:outputText value="#{registro.discente.pessoa.municipioNaturalidadeOutro}"/>
		</td>
	</tr>
	<c:if test="${registro.discente.graduacao}">
		<tr>
			<th>Curso:</th>
			<td>
				<h:outputText value="#{registro.discente.curso.descricao}"/>
			</td>
			<th>Grau:</th>
			<td>
				<h:outputText value="#{matrizCurricularDiscenteGraduacao.grauAcademico.descricao}"/>
			</td>
		</tr>
	</c:if>
	<c:if test="${registro.discente.stricto}">
		<tr>
			<th>Curso:</th>
			<td>
				<h:outputText value="#{registro.discente.curso.descricao}"/>
			</td>
			<th>Grau:</th>
			<td>
				<h:outputText value="#{registro.discente.curso.titulacaoMasculino}"/>
			</td>
		</tr>
	</c:if>
	<tr>
		<th>Concluído em:</th>
		<td>
			<fmt:formatDate value="${registro.dataColacao}" pattern="dd/MM/yyyy"/>
		</td>
		<th> Expedido em: </th>
		<td>
			<fmt:formatDate value="${registro.dataExpedicao}" pattern="dd/MM/yyyy"/>
		</td>
	</tr>
	<tr>
		<th><h:outputText value="#{registro.assinaturaDiploma.descricaoFuncaoReitor}"/>:</th>
		<td>
			<h:outputText value="#{registro.assinaturaDiploma.nomeReitor}"/>
		</td>
		<th valign="top">Estabelecimento:</th>
		<td>
			<h:outputText value="#{registro.livroRegistroDiploma.instituicao}"/>
		</td>
	</tr>
	<tr>
		<c:choose>
			<c:when test="${ impressaoDiploma.latoSensu }">
				<th>Assinatura no Verso:</th>
				<td>
					<h:outputText value="#{registro.assinaturaDiploma.nomeResponsavelCertificadosLatoSensu}"/>
				</td>
			</c:when>
			<c:otherwise>
				<th><h:outputText value="#{registro.assinaturaDiploma.descricaoFuncaoDiretorUnidadeDiplomas}"/>:</th>
				<td>
					<h:outputText value="#{registro.assinaturaDiploma.nomeDiretorUnidadeDiplomas}"/>
				</td>
			</c:otherwise>
		</c:choose>
		<th> Data do Registro: </th>
		<td >
			<fmt:formatDate value="${registro.dataRegistro}" pattern="dd/MM/yyyy"/>
		</td>
	</tr>
	<tr>
		<th>Registrado por:</th>
		<td>
			<h:outputText value="#{registro.registroEntrada.usuario.pessoa.nome}"/>
		</td>
		<th> Processo: </th>
		<td>
			<h:outputText value="#{registro.processo}"/>
		</td>
	</tr>
	<tr>
		<th valign="top"> Observação: </th>
		<td colspan="3">
			<c:forEach items="#{registro.observacoesAtivas}" var="observacao">
				<h:outputText value="#{observacao.observacao}"/>&nbsp;
			</c:forEach>
		</td>
	</tr>
</table>
<br/>
<table class="formulario" >
	<caption>Dados para Emissão da Segunda Via</caption>
	<tbody>
		<tr>
			<th class="${ impressaoDiploma.protocoloAtivo ? 'required' : '' }"> Número do Processo: </th>
			<td ><h:inputText value="#{impressaoDiploma.processo}" onkeyup="formatarProtocolo(this, event);" size="20" maxlength="20" id="processo"/><br>
				(ex.: 23077.001234/2003-98) Caso não saiba os dígitos verificadores, informe 99
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
			<h:commandButton id="btnGerar" value="Gerar Segunda Via do Diploma" action="#{impressaoDiploma.gerarSegundaVia}" onclick="$('form:processo').readOnly = true;"/>
			<h:commandButton id="btnSelecionar" value="<< Escolher Outro Discente" action="#{impressaoDiploma.iniciarImpressaoSegundaVia}"/>
			<h:commandButton id="btnCancelar" value="Cancelar" action="#{registroDiplomaColetivo.cancelar}" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
</table>
<br>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena">Campos de preenchimento obrigatório. </span>
</center>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>