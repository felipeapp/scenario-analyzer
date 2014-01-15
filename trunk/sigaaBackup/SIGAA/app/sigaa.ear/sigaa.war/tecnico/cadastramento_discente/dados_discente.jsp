<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="consultaDadosDiscentesIMD"></a4j:keepAlive>
<h:form>
<h2><ufrn:subSistema /> &gt; <h:commandLink action="#{ consultaDadosDiscentesIMD.iniciarBusca }" value="Consultar Dados dos Discentes" /> &gt; Visualizar Discente</h2>

<table class="visualizacao" style="width:70%;">
	<caption>Dados do Discente</caption>
	<tr><th>Matrícula:</th><td><h:outputText value="#{consultaDadosDiscentesIMD.discente.discente.matricula}" /></td></tr>
	<tr><th>Nome:</th><td><h:outputText value="#{consultaDadosDiscentesIMD.discente.discente.pessoa.nome}" /></td></tr>
	<tr><th>Email:</th><td><h:outputText value="#{consultaDadosDiscentesIMD.discente.discente.pessoa.email}" /></td></tr>
	<tr><th>CPF:</th><td><ufrn:format type="cpf" valor="${consultaDadosDiscentesIMD.discente.discente.pessoa.cpf_cnpj}" /></td></tr>
	<tr><th>RG:</th><td><h:outputText value="#{consultaDadosDiscentesIMD.discente.discente.pessoa.registroGeral}" /></td></tr>
	<tr><th>Nascimento:</th><td><h:outputText value="#{consultaDadosDiscentesIMD.discente.discente.pessoa.dataNascimento}" /></td></tr>
	<tr><th>Classificação:</th><td><h:outputText value="#{consultaDadosDiscentesIMD.discente.resultado.classificacaoAprovado}" /></td></tr>
	<tr><th>Res. Vagas:</th><td><h:outputText value="#{consultaDadosDiscentesIMD.discente.inscricaoProcessoSeletivo.reservaVagas ? 'Sim' : 'Não'}" /></td></tr>
	<tr><th>Convocação:</th><td><h:outputText value="#{consultaDadosDiscentesIMD.discente.convocacaoProcessoSeletivo.descricao}" /></td></tr>
	<tr><th>Pólo / Grupo:</th><td><h:outputText value="#{consultaDadosDiscentesIMD.discente.inscricaoProcessoSeletivo.opcao.descricao}" /></td></tr>
	<tr><th>Turma:</th><td><h:outputText value="#{consultaDadosDiscentesIMD.discente.discente.turmaEntradaTecnico.especializacao.descricao}" /></td></tr>
	<tr><th>Status:</th><td><h:outputText value="#{consultaDadosDiscentesIMD.discente.discente.statusString}" /></td></tr>
	<tr><th>&nbsp;</th><td><h:outputText value="#{consultaDadosDiscentesIMD.discente.cancelamento.observacoes}" /></td></tr>

	<tfoot>
		<tr>
			<td colspan="2" style="text-align: center;">
				<h:commandButton value="<< Voltar" action="#{ consultaDadosDiscentesIMD.buscar }" id="btnVoltar" />
			</td>
		</tr>
	</tfoot>
</table>


</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>