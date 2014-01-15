<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > 
	<h:outputText value="Definição de Leiaute do Arquivo de Importação" rendered="#{ importaAprovadosTecnicoMBean.definicaoLeiaute }" />
	<h:outputText value="Importar Aprovados de Vestibulares Externos" rendered="#{ !importaAprovadosTecnicoMBean.definicaoLeiaute }" />
	</h2>

	<div class="descricaoOperacao">
		<p><b>Caro Usuário,</b></p>
		<p>Confirme se os dados do mapeamento para o leiaute do arquivo com dados a serem importados está correto.</p>
	</div>
	<br/>
	<h:form id="form">
		<table class="formulario" width="75%">
			<caption>Mapeamento dos Dados em Atributos do Sistema</caption>
			<tbody>
				<tr>
					<th width="50%" class="rotulo">Descrição: </th>
					<td> ${ importaAprovadosTecnicoMBean.leiauteArquivoImportacao.descricao }</td>
				</tr>
				<tr>
					<th class="rotulo">Forma de Ingresso: </th>
					<td> ${ importaAprovadosTecnicoMBean.leiauteArquivoImportacao.formaIngresso.descricao }</td>
				</tr>
				<tr>
					<th class="rotulo">Ativo: </th>
					<td> <ufrn:format type="simNao" valor="${ importaAprovadosTecnicoMBean.leiauteArquivoImportacao.ativo }" /></td>
				</tr>
				<tr>
					<td colspan="2" class="subFormulario">Atributos Mapeados</td>
				</tr>
				<tr class="caixaCinza">
					<th style="font-weight: bold; text-align: left;">Campo de Dados do Arquivo</td>
					<th style="font-weight: bold; text-align: left;">Atributo Mapeado</td>
				</tr>
				<c:forEach items="#{ importaAprovadosTecnicoMBean.atributosMapeados }" var="item" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${item.key}</td>
						<td>${item.value.descricao}</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" style="text-align: center">
						<h:commandButton value="Cadastrar" action="#{importaAprovadosTecnicoMBean.cadastraLeiaute}" id="cadastrar"/>
						<h:commandButton value="<< Voltar" action="#{importaAprovadosTecnicoMBean.formMapeamento}"  id="voltar"/>
						<h:commandButton value="Cancelar" action="#{importaAprovadosTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		</h:form>
	<br>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>