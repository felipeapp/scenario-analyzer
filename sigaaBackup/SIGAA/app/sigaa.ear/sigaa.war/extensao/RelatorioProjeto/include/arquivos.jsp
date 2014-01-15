<table class="formulario" style="width: 100%">
	
		<tr>
			<td colspan="2" class="subFormulario"> Anexar Arquivo com outros detalhes da execução da ação </td>
		</tr>	

		<tr>
			<th  class="required"> Descrição:</th>
			<td>
				<h:inputText  id="descricao" value="#{relatorioAcaoExtensao.mbean.descricaoArquivo}" size="60" maxlength="90" readonly="#{relatorioAcaoExtensao.mbean.readOnly}" onchange="setAba('arquivos')" />
			</td>
		</tr>
		
		
		<tr>
			<th class="required" width="20%">Arquivo:</th>
			<td>
				<t:inputFileUpload id="uFile" value="#{relatorioAcaoExtensao.mbean.file}" storage="file" disabled="#{relatorioAcaoExtensao.mbean.readOnly}" size="50"/>
			</td>
		</tr>

		<tr>
			<td colspan="3">
				<center>
					<h:commandButton action="#{relatorioAcaoExtensao.mbean.anexarArquivo}" value="Anexar Arquivo" id="btAnexarArqui" 
						disabled="#{relatorioAcaoExtensao.mbean.readOnly}" onclick="this.form.target='_self';setAba('arquivos');"/>
				</center>
			</td>
		</tr>

		<tr>
			<td colspan="3">
				<div class="infoAltRem">
				    <h:graphicImage value="/img/delete.gif" style="overflow: visible;" styleClass="noborder"/>: Remover Arquivo
				    <h:graphicImage value="/img/view.gif" style="overflow: visible;" styleClass="noborder"/>: Ver Arquivo				    
				</div>
			</td>
		</tr>	
		
		<tr>
			<td colspan="2">
			<input type="hidden" value="0" id="idArquivo" name="idArquivo"/>
			<input type="hidden" value="0" id="idArquivoRelatorio" name="idArquivoRelatorio"/>
			
				<t:dataTable id="dataTableArq" value="#{relatorioAcaoExtensao.mbean.arquivosRelatorio}" var="anexo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
					<t:column  width="97%">
						<f:facet name="header"><f:verbatim>Descrição do Arquivo</f:verbatim></f:facet>
						<h:outputText value="#{anexo.descricao}" />
					</t:column>
	
					<t:column>
						<h:commandButton image="/img/delete.gif" action="#{relatorioAcaoExtensao.mbean.removeAnexo}" title="Remover Arquivo"  alt="Remover Arquivo" id="remArquivo"
							onclick="this.form.target='_self';$(idArquivo).value=#{anexo.idArquivo};$(idArquivoRelatorio).value=#{anexo.id};#{confirmDelete}"/>
					</t:column>
					<t:column>
						<h:commandButton image="/img/view.gif" action="#{relatorioAcaoExtensao.mbean.viewArquivo}" title="Ver Arquivo"  alt="Ver Arquivo" 
							onclick="$(idArquivo).value=#{anexo.idArquivo}; this.form.target='_blank'" id="viewArquivo" />
					</t:column>	
				</t:dataTable>
			</td>
		</tr>		
		
</table>