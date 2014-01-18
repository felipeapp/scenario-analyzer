<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages />
	<h2><ufrn:subSistema /> > Cadastro de Avisos/Notícias de Processos Seletivos</h2>

	<h:form id="form" enctype="multipart/form-data">
		<c:set var="readOnly" value="#{avisoProcessoSeletivoVestibular.readOnly}" />
		<table class=formulario width="100%">
			<caption class="listagem">Dados do Aviso/Notícia</caption>
			<tr>
				<th class="required">Processo Seletivo:</th>
				<td><h:selectOneMenu id="processoSeletivo"
					value="#{avisoProcessoSeletivoVestibular.obj.processoSeletivo.id}"
					onchange="submit()"
					disabled="#{avisoProcessoSeletivoVestibular.readOnly}"
					readonly="#{avisoProcessoSeletivoVestibular.readOnly}"
					valueChangeListener="#{avisoProcessoSeletivoVestibular.carregaListaAvisos}">
					<f:selectItem itemValue="0" itemLabel="---> SELECIONE <---" />
					<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Chamada:</th>
				<td><h:inputText id="chamada"
					value="#{avisoProcessoSeletivoVestibular.obj.chamada}" size="60"
					readonly="#{avisoProcessoSeletivoVestibular.readOnly}"
					maxlength="250" />
				</td>
			</tr>
			<tr>
				<th valign="top">Corpo:</th>
				<td><h:inputTextarea id="corpo"
					value="#{avisoProcessoSeletivoVestibular.obj.corpo}" cols="60" rows="6"
					readonly="#{avisoProcessoSeletivoVestibular.readOnly}" />
				</td>
			</tr>
			<tr>
				<th class="required">Data de Publicação:</th>
				<td><t:inputCalendar id="dataPublicacao" renderAsPopup="true"
					renderPopupButtonAsImage="true" size="10" maxlength="10"
					onkeypress="return formataData(this,event)" 
					readonly="#{avisoProcessoSeletivoVestibular.readOnly}"
					disabled="#{avisoProcessoSeletivoVestibular.readOnly}"
					value="#{avisoProcessoSeletivoVestibular.dataPublicacao}" />
					as
					<h:inputText value="#{avisoProcessoSeletivoVestibular.horaPublicacao}" maxlength="5" size="5" id="hora"
								readonly="#{avisoProcessoSeletivoVestibular.readOnly}"
								onkeyup="return formataHora(this, event, null)">
								<f:convertDateTime pattern="HH:mm" />
					</h:inputText> 
				</td>
			</tr>
			<tr>
				<th valign="top">Arquivo Anexo:</th>
				<td>
					<t:inputFileUpload value="#{avisoProcessoSeletivoVestibular.arquivo}" style="width:100%;" 
						disabled="#{avisoProcessoSeletivoVestibular.readOnly}" readonly="#{avisoProcessoSeletivoVestibular.readOnly}"/><br/>
					<h:outputText rendered="#{not empty avisoProcessoSeletivoVestibular.obj.idArquivo}" value="Este aviso já possui um anexo. A inclusão de um arquivo substitui o anterior"/> 
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="<< Escolher Outro" action="#{avisoProcessoSeletivoVestibular.listar}" rendered="#{avisoProcessoSeletivoVestibular.voltarLista}"/>
						<h:commandButton value="#{avisoProcessoSeletivoVestibular.confirmButton}" action="#{avisoProcessoSeletivoVestibular.cadastrar}" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{avisoProcessoSeletivoVestibular.cancelar}" immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<center><html:img page="/img/required.gif"
			style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigatório. </span> <br>
		</center>
		<br />
		<c:if test="${not empty avisoProcessoSeletivoVestibular.obj.processoSeletivo.avisos}">
			<table class="listagem">
				<caption>Lista de Avisos/Notícias cadastrados (${fn:length(avisoProcessoSeletivoVestibular.obj.processoSeletivo.avisosAtivos)})</caption>
				<thead>
					<tr>
						<th style="text-align: center" width="15%">Início da Publicação</th>
						<th>Chamada / Corpo</th>
						<th style="text-align: center">Anexo</th>
					</tr>
				</thead>
				<tbody>
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
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>