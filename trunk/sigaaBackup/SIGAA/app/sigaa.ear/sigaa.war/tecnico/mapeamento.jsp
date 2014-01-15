<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > 
	<h:outputText value="Definição de Leiaute do Arquivo de Importação" rendered="#{ importaAprovadosTecnicoMBean.definicaoLeiaute }" />
	<h:outputText value="Importar Aprovados de Vestibulares Externos" rendered="#{ !importaAprovadosTecnicoMBean.definicaoLeiaute }" />
	</h2>

	<div class="descricaoOperacao">
		<p><b>Caro Usuário,</b></p>
		<p>Indique qual campo do arquivo importado será definido ao atributo do Discente.</p>
	</div>
	<br/>
	<h:form id="form">
		<div class="infoAltRem">
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover
		</div>
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
					<th class="rotulo">A primeira linha do arquivo possui cabeçalho: </th>
					<td> <ufrn:format type="simNao" valor="${ importaAprovadosTecnicoMBean.leiauteArquivoImportacao.possuiCabecalho }" /></td>
				</tr>
				<tr>
					<td class="subFormulario" colspan="3">Atributos mapeados</td>
				</tr>
				<tr class="caixaCinza">
					<td style="font-weight: bold;">Campo de Dados do Arquivo</td>
					<td style="font-weight: bold;">Atributo Mapeado</td>
					<td width="2%"></td>
				</tr>
				<c:forEach items="#{ importaAprovadosTecnicoMBean.atributosMapeados }" var="item" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${item.key}</td>
						<td>${item.value.descricao}</td>
						<td>
							<h:commandLink action="#{ importaAprovadosTecnicoMBean.removerMapeamento }" title="Remover" onclick="#{ confirmDelete }">
								<img src="/shared/img/delete.gif" style="overflow: visible;" />
								<f:param name="atributo" value="#{ item.value.atributo }"/>
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
				<c:if test="${ not empty importaAprovadosTecnicoMBean.camposCombo and not empty importaAprovadosTecnicoMBean.atributosCombo}">
					<tr>
						<td>
							<h:selectOneMenu value="#{ importaAprovadosTecnicoMBean.campo }" id="campo">
								<f:selectItems value="#{ importaAprovadosTecnicoMBean.camposCombo }"/>
							</h:selectOneMenu>
						</td>
						<td colspan="2">
							<h:selectOneMenu value="#{ importaAprovadosTecnicoMBean.atributo }" id="atributo">
								<f:selectItems value="#{ importaAprovadosTecnicoMBean.atributosCombo }"/>
							</h:selectOneMenu>
						</td>
					</tr>
					<tr>
						<td colspan="3" class="caixaCinza" style="text-align: center;">
							<h:commandButton value="Adicionar" action="#{importaAprovadosTecnicoMBean.adicionarMapeamento}" id="adicionar"/>
						</td>
					</tr>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="<< Voltar" action="#{importaAprovadosTecnicoMBean.formUpload}"  id="voltar"/>
						<h:commandButton value="Cancelar" action="#{importaAprovadosTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar"/>
						<h:commandButton value="Próximo Passo >>" action="#{importaAprovadosTecnicoMBean.submeterMapeamento}" id="submeterMapeamento"/>
					</td>
				</tr>
			</tfoot>
		</table>
		</h:form>
	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>
	<br>
	<br>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>