<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:form id="form">
<div class="descricaoOperacao">
<p><b>Caro Usu�rio,</b></p>
<p>Confirme se todos os dados abaixo est�o corretos antes de concluir a opera��o.</p>
</div>

<table class="formulario" width="80%">
	<caption>Dados do Registro</caption>
	<tbody>
		<tr>
			<th width="45%" class="rotulo">Discente:</th>
			<td>
				<h:outputText value="#{registroDiplomaAntigoMBean.obj.discente.matriculaNome}"/>
			</td>
		</tr>
		<tr>
			<th class="rotulo"> Livro: </th>
			<td>
				<h:outputText value="#{registroDiplomaAntigoMBean.livro.titulo} - #{registroDiplomaAntigoMBean.obj.discente.curso}"/>
			</td>
		</tr>
		<tr>
		<th class="rotulo"> Folha: </th>
			<td>
				<h:outputText value="#{registroDiplomaAntigoMBean.folha.numeroFolha}"/>
			</td>
		</tr>
		<tr>
			<th class="rotulo"> N� do Registro: </th>
			<td><h:outputText value="#{registroDiplomaAntigoMBean.obj.numeroRegistro}"/>
		</tr>
		<tr>
			<th class="rotulo"> N�mero do Protocolo: </th>
			<td ><h:outputText value="#{registroDiplomaAntigoMBean.obj.processo}" />
			</td>
		</tr>
		<tr>
			<th class="rotulo">Data da Cola��o:</th>
			<td>
				<ufrn:format type="dia_mes_ano" valor="${registroDiplomaAntigoMBean.obj.dataColacao}" />
			</td>
		</tr>
		<tr>
			<th class="rotulo"> Data do Registro: </th>
			<td>
				<ufrn:format type="dia_mes_ano" valor="${registroDiplomaAntigoMBean.obj.dataRegistro}"/>
			</td>
		</tr>
		<tr>
			<th class="rotulo"> Data de Expedi��o: </th>
			<td>
				<ufrn:format type="dia_mes_ano" valor="${registroDiplomaAntigoMBean.obj.dataExpedicao}"/>
			</td>
		</tr>
		<tr>
			<th  class="rotulo" valign="top">Observa��o: </th>
			<td>
				<h:outputText value="#{registroDiplomaAntigoMBean.observacao.observacao}" />
			</td>
		</tr>
		<tr>
			<td colspan="3" class="subFormulario">Assinaturas do Diploma</td>
		</tr>
		<tr>
			<th valign="top" class="rotulo">${registroDiplomaAntigoMBean.obj.assinaturaDiploma.descricaoFuncaoReitor}:</th>
			<td> ${registroDiplomaAntigoMBean.obj.assinaturaDiploma.nomeReitor}</td>
		</tr>
		<tr>
			<th class="rotulo">${registroDiplomaAntigoMBean.obj.assinaturaDiploma.descricaoFuncaoDiretorUnidadeDiplomas}:</th>
			<td>${registroDiplomaAntigoMBean.obj.assinaturaDiploma.nomeDiretorUnidadeDiplomas}</td>
		</tr>
		<tr>
			<th class="rotulo">${registroDiplomaAntigoMBean.obj.assinaturaDiploma.descricaoFuncaoDiretorGraduacao}:</th>
			<td>${registroDiplomaAntigoMBean.obj.assinaturaDiploma.nomeDiretorGraduacao}</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton action="#{registroDiplomaAntigoMBean.cadastrar}" value="Cadastrar" id="cadastrar"/>
				<h:commandButton action="#{registroDiplomaAntigoMBean.formAssinaturas}" value="<< Voltar" id="voltar"/>
				<h:commandButton action="#{registroDiplomaAntigoMBean.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>