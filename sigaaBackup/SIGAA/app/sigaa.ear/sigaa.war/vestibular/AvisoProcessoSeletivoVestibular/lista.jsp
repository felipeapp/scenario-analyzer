<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Avisos/Notícias dos Processos Seletivos</h2>
	<h:messages />
	<h:form>
		<table class=formulario width="65%">
			<caption class="listagem">Filtrar Avisos/Notícias do Processo Seletivo</caption>
			<tr>
				<th class="required">Processo Seletivo:</th>
				<td><h:selectOneMenu id="processoSeletivo"
					value="#{avisoProcessoSeletivoVestibular.obj.processoSeletivo.id}"
					onchange="submit()"
					valueChangeListener="#{avisoProcessoSeletivoVestibular.carregaListaAvisos}">
					<f:selectItem itemValue="0" itemLabel="---> SELECIONE <---" />
					<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
				</h:selectOneMenu></td>
			</tr>
		</table>
		<br/>
		<center><html:img page="/img/required.gif"
			style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigatório. </span> <br>
		</center>
		<br />
		<c:if test="${not empty avisoProcessoSeletivoVestibular.obj.processoSeletivo.avisosAtivos}">
			<div class="infoAltRem" align="center"><h:graphicImage value="/img/alterar.gif"
			style="overflow: visible;" />: Alterar dados do aviso <h:graphicImage
			value="/img/delete.gif" style="overflow: visible;" />: Remover aviso <br />
			</div>
			<table class=listagem>
				<caption class="listagem">Lista de Avisos/Notícias dos Processos Seletivos</caption>
				<thead>
					<tr>
						<th style="text-align: center" width="15%">Início da Publicação</th>
						<th>Chamada / Corpo</th>
						<th style="text-align: center">Anexo</th>
						<th colspan="2">Operar</th>
					</tr>
				</thead>
				<c:forEach items="#{avisoProcessoSeletivoVestibular.obj.processoSeletivo.avisosAtivos}" var="item"
					varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td style="text-align: center; vertical-align: top" width="15%">
							<h:outputText value="#{item.inicioPublicacao}">
								<f:convertDateTime dateStyle="short" timeStyle="short"/>
							</h:outputText>
						</td>
						<td>
							${item.chamada}<br/>
							${item.corpo}
						</td>
						<td align="center" valign="top">
							<c:if test="${not empty item.idArquivo}">
								<a href="${ctx}/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo)}" target="_blank">
									<h:graphicImage value="/img/icones/page_white_magnify.png" style="vertical-align: middle; overflow: visible;" /><br />
								</a>
							</c:if>
						</td>
						<td width="2%" valign="top"><h:commandLink title="Alterar"
							action="#{avisoProcessoSeletivoVestibular.atualizar}"
							style="border: 0;">
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/alterar.gif" />
						</h:commandLink></td>
						<td width="2%" valign="top"><h:commandLink title="Remover"
							action="#{avisoProcessoSeletivoVestibular.remover}"
							onclick="#{confirmDelete}"
							style="border: 0;">
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/delete.gif" />
						</h:commandLink></td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
		<c:if test="${empty avisoProcessoSeletivoVestibular.obj.processoSeletivo.avisosAtivos}">
			<center><h:outputText value="Não há avisos cadastrados para este Processo Seletivo"/></center>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>