<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > 
	<h:outputText value="Defini��o de Leiaute do Arquivo de Importa��o" />
	</h2>

	<div class="descricaoOperacao">
	<p><b>Caro Usu�rio,</b></p>
	<p>Utilize este formul�rio para remover algum campo do leiaute cadastrado se este n�o for mais utilizado. Caso seja necess�rio acrescentar novos campos,
	voc� dever� carregar o arquivo que ser� utilizado para a importa��o dos candidatos aprovados.
	</div>
	<br/>
	<h:form id="form">
		<div class="infoAltRem">
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover
		</div>
		<table class="visualizacao" width="75%">
			<caption>Mapeamento dos Dados em Atributos do Sistema</caption>
			<tbody>
				<tr>
					<th width="50%">Descri��o: </th>
					<td> ${ importaAprovadosOutrosVestibularesMBean.leiauteArquivoImportacao.descricao }</td>
				</tr>
				<tr>
					<th>Forma de Ingresso: </th>
					<td> ${ importaAprovadosOutrosVestibularesMBean.leiauteArquivoImportacao.formaIngresso.descricao }</td>
				</tr>
				<tr>
					<th>A primeira linha do arquivo possui cabe�alho: </th>
					<td> <ufrn:format type="simNao" valor="${ importaAprovadosOutrosVestibularesMBean.leiauteArquivoImportacao.possuiCabecalho }" /></td>
				</tr>
				<tr>
					<td colspan="2" class="subFormulario">Atributos Mapeados</td>
				</tr>
				<tr>
					<td colspan="2">
						<table class="listagem" width="100%">
							<thead>
								<tr>
									<th width="2%">Item</th>
									<th style="text-align: left;">Campo do Arquivo</th>
									<th style="text-align: left;">Atributo Mapeado</th>
									<th width="2%"></th>
								</tr>
							</thead>
							<tbody>
							<c:forEach items="#{ importaAprovadosOutrosVestibularesMBean.atributosMapeados }" var="item" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td  style="text-align:right;">${status.index + 1 }</td>
									<td>${item.key}</td>
									<td>${item.value.descricao}</td>
									<td>
										<h:commandLink action="#{ importaAprovadosOutrosVestibularesMBean.removerMapeamento }" title="Remover" onclick="#{ confirmDelete }">
											<img src="/shared/img/delete.gif" style="overflow: visible;" />
											<f:param name="atributo" value="#{ item.value.atributo }"/>
										</h:commandLink>
									</td>
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
						<h:commandButton value="Carregar um arquivo" action="#{importaAprovadosOutrosVestibularesMBean.formUpload}"  id="carregar"/>
						<h:commandButton value="Cancelar" action="#{importaAprovadosOutrosVestibularesMBean.cancelar}" onclick="#{confirm}" id="cancelar"/>
						<h:commandButton value="Pr�ximo Passo >>" action="#{importaAprovadosOutrosVestibularesMBean.submeterMapeamento}" id="submeterMapeamento"/>
					</td>
				</tr>
			</tfoot>
		</table>
		</h:form>
	<br>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>