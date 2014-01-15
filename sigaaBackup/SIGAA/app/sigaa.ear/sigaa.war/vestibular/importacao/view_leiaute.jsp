<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > 
	<h:outputText value="Definição de Leiaute do Arquivo de Importação" />
	</h2>

	<br/>
	<h:form id="form">
		<table class="visualizacao" width="75%">
			<caption>Mapeamento dos Dados em Atributos do Sistema</caption>
			<tbody>
				<tr>
					<th width="50%">Descrição: </th>
					<td> ${ importaAprovadosOutrosVestibularesMBean.leiauteArquivoImportacao.descricao }</td>
				</tr>
				<tr>
					<th>Forma de Ingresso: </th>
					<td> ${ importaAprovadosOutrosVestibularesMBean.leiauteArquivoImportacao.formaIngresso.descricao }</td>
				</tr>
				<tr>
					<th>Ativo: </th>
					<td> <ufrn:format type="simNao" valor="${ importaAprovadosOutrosVestibularesMBean.leiauteArquivoImportacao.ativo }" /></td>
				</tr>
				<tr>
					<td colspan="2" class="subFormulario">Atributos Mapeados</td>
				</tr>
				<tr>
					<td colspan="2">
						<table class="listagem" width="100%">
							<thead>
								<tr>
									<th style="text-align: left;">Campo do Arquivo</th>
									<th style="text-align: left;">Atributo Mapeado</th>
								</tr>
							</thead>
							<tbody>
							<c:forEach items="#{ importaAprovadosOutrosVestibularesMBean.leiauteArquivoImportacao.mapeamentoAtributos }" var="item" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td style="text-align: left;" width="47%">${item.campo}</td>
									<td style="text-align: left;" width="47%">${item.atributoMapeavel.descricao}</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" class="formulario" style="text-align: center">
						<h:commandButton value="<< Voltar" action="#{importaAprovadosOutrosVestibularesMBean.listarLeiautes}"  id="voltar"/>
						<h:commandButton value="Cancelar" action="#{importaAprovadosOutrosVestibularesMBean.cancelar}" onclick="#{confirm}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		</h:form>
	<br>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>