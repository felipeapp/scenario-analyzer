<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<style type="text/css">

.link{
	color: #003395;
	font-size: inherit;
	font-weight: bold;
	cursor: pointer;
}

</style>

<script type="text/javascript">
	
	function substituirBarra(idTextArea) {
		obj = document.getElementById(idTextArea);
		obj.value = obj.value.replaceAll('$', '|');
	}
	
	function substituirContraBarra(idTextArea) {
		obj = document.getElementById(idTextArea);
		obj.value = obj.value.replaceAll('\\', '|');
	}
	
	function substituirUnderLine(idTextArea) {
		obj = document.getElementById(idTextArea);
		obj.value = obj.value.replaceAll('#', '_');
	}
	
	
</script>

<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoBibliografica}">
<h2>  <ufrn:subSistema /> &gt; Importar T�tulos </h2>
</c:if>

<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoAutoridades}">
<h2>  <ufrn:subSistema /> &gt; Importar Autoridades </h2>
</c:if>

<div class="descricaoOperacao">
	<p> Escolha um arquivo no formato <strong>MARC 21</strong> ou digite os dados do t�tulo no espa�o destinado.</p> 
	
	<br/><br/>
	<p><strong>OBSERVA��ES:</strong></p>
	
	<p> Biblioteca de opera��o � utilizada para fins de relat�rio.
		<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoBibliografica}">
			Os t�tulos
		</c:if>
		<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoAutoridades}">
			As autoridades
		</c:if>
		ser�o mostradas como sendo importadas pela biblioteca escolhida.
	</p>
	
	<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoBibliografica}">
		<p> O sistema suporta a importa��o de v�rios T�tulos em um �nico arquivo MARC, nesse caso todos os T�tulos ser�o salvos como
		<strong>"N�o Catalogados"</strong>. O bibliotec�rio poder�, em um momento posterior, trabalhar cada T�tulo individualmente e finalizar a sua cataloga��o.</p>
	</c:if>
	
	<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoAutoridades}">
		<p> O sistema suporta a importa��o de v�rias Autoridades em um �nico arquivo MARC, nesse caso todas as Autoridades ser�o salvas como
		<strong>"N�o Catalogadas"</strong>. O bibliotec�rio poder�, em um momento posterior, trabalhar cada Autoridade individualmente e finalizar a sua cataloga��o.</p>
	</c:if>
	
	<p> Por padr�o os campos locais - 09X, 59X, 69X e 9XX - s�o ignorados, pois n�o tem sentido para as bibliotecas do sistema, se quiser import�-los, mude o valor dessa op��o para SIM.</p>
</div>


<f:view>

	<a4j:keepAlive beanName="cooperacaoTecnicaImportacaoMBean"></a4j:keepAlive>

	<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>

	<%-- Para o usu�rio usar o bot�o voltar e as informa��o da pesquisa dos t�tulos ainda est� l�. --%>
	<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>

	<%-- Mant�m as informa��o sobre a cataloga��o .--%>
	<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>


	<%-- Para quando a p�gina de pesquisa de autoridades chama a importa��o e o usu�rio volta para a p�gina de pesquisa --%>
	
	<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoAutoridades}">
		
		<a4j:keepAlive beanName="catalogaAutoridadesMBean"></a4j:keepAlive>
		
	</c:if>

	<h:form id="formImportar" enctype="multipart/form-data">

		<h:messages showDetail="true" />

		<table class="formulario" width="100%"  style="border-bottom: 0px solid;">
			
			<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoBibliografica}"> 
				<caption class="listagem">Importar T�tulo</caption>
			</c:if>
			
			<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoAutoridades}">
				<caption class="listagem">Importar Autoridade</caption> <%-- Autoridade n�o tem formato --%>
			</c:if>
			
		</table>

			<table class="formulario" width="100%" style="border-top: 0px solid">
	
				<%--       seleciona a biblioteca     --%>
				<tr>
					<th colspan="2" style="width: 35%" class="obrigatorio">Biblioteca de Opera��o:</th>
	
					<td colspan="5" style="width: 65%">
						<h:selectOneMenu value="#{cooperacaoTecnicaImportacaoMBean.biblioteca}" id="somBiblioteca" required="true">
							<c:if test="${fn:length(cooperacaoTecnicaImportacaoMBean.bibliotecaList) > 1}">
								<f:selectItem itemLabel=" -- Selecione -- " itemValue="-1" />
							</c:if>  
							<f:selectItems value="#{cooperacaoTecnicaImportacaoMBean.bibliotecaList}"/>
	    				</h:selectOneMenu>  
					</td>					
				</tr>
	
				<%--       seleciona a forma     --%>
				<tr>
					<th colspan="2" style="width: 35%" class="obrigatorio">Forma de Importa��o: </th>
	
					<td colspan="5" style="width: 65%">
						<h:selectOneRadio value="#{cooperacaoTecnicaImportacaoMBean.tipoImportacao}" valueChangeListener="#{cooperacaoTecnicaImportacaoMBean.atualizaPagina}" onclick="submit();">  
	        				<f:selectItem itemLabel="Arquivo" itemValue="1" />  
	        				<f:selectItem itemLabel="Digitar os dados no Formato MARC 21" itemValue="2" />
	    				</h:selectOneRadio>  
					</td>
					
				</tr>
	
	
				<tr style="text-align:center;">
					
					<th colspan="2" class="obrigatorio">Importar Campos Locais ? </th>
					
					<td colspan="5" style="margin-bottom:20px; text-align:left;">
						
						<h:selectOneRadio value="#{cooperacaoTecnicaImportacaoMBean.importarCamposLocais}" onclick="submit();">  
	        				<f:selectItem itemLabel="N�O" itemValue="false" />
	        				<f:selectItem itemLabel="SIM" itemValue="true" />
	    				</h:selectOneRadio>
					</td>
				</tr>
				
		
				<%-- mostrado quando o usuario escolhe arquivo  --%>
				
				
				<tr style="text-align:center;">
					<c:if test="${cooperacaoTecnicaImportacaoMBean.importacaoArquivo}">
						<th colspan="2" class="obrigatorio">Arquivo:</th>
					
						<td colspan="5" style="margin-bottom:20px; text-align:left;">
							<t:inputFileUpload id="arquivo" value="#{cooperacaoTecnicaImportacaoMBean.arquivo}" size="60" 
							rendered="#{cooperacaoTecnicaImportacaoMBean.importacaoArquivo}"/>
						</td>
					</c:if>
				</tr>
				
				
				<%-- mostrado quando o usuario escolhe digitar dados  --%>
				
				
				<tr>
					<td colspan="7" >
						<c:if test="${cooperacaoTecnicaImportacaoMBean.importacaoDados}">
							<div class="descricaoOperacao">
		                    	<p>
								 Para a digita��o dos dados diretamente utilizamos o padr�o adotado pela <strong>Biblioteca Nacional</strong>. 
								</p>
								<p>
								Deve-se digitar cada campo em uma linha separada. O tr�s primeiros caracteres de uma linha 
		                        s�o sempre a etiqueta do campo. No caso de campos de dados os pr�ximos dois caracteres devem 
		                        ser os indicadores. Caso algum deles n�o exista, deve-se colocar o caractere '_'. 
		                        Os subcampos s�o limitados pelo caractere '|', ou seja, tudo que estiver entre dois caracteres '|' at� o final 
		                        de uma linha � considerado dado de um subcampo. Nos dados dos subcampos n�o pode existir nenhum caractere '|'.
								<p><strong>Certifique-se de que seu navegador esteja com a configura��o UTF-8 para os acentos serem interpretados corretamente.</strong></p>
								<p>Segue abaixo um pequeno modelo de como deve-se digitar os dados: </p> <br/><br/>  
								<p>
									<br/>
									<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoBibliografica}">
										LDR	00000namaa22000001&nbsp 4500<br/>
										001 	13076<br/>
										005 	00000000000000.0<br/>
										008	yymmddb2009&nbsp&nbsp&nbsp&nbspspba&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp|001|&nbsppor&nbsp&nbspd<br/>
										080 __ |a 12345<br/>
										100 1_ |a Sobrenome, Nome.<br/>
										245 14 |a Titulo : |b subtitulo  / |c Complem. do t�tulo.<br/>
										260 __ |a Local. : |b Editora, |c 2000.<br/>
									</c:if>
									<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoAutoridades}">
										LDR	00000namaa22000001# 4500<br/>
										001 	13076<br/>
										008	yymmddb2009#&nbsp&nbsp#012#&nbsp&nbsp&nbsp##&nbsp&nbsp&nbsp&nbsp#001#1#eng##<br/>
										100 	1_ |a Sobrenome, Nome.<br/>
										400 	__ |a Sobrenome, Nome.<br/>
										400 	__ |a Sobrenome, Nome.<br/>
									</c:if>
								</p>	
							</div>
						</c:if>
					</td>
				</tr>

				<tr>
					<c:if test="${cooperacaoTecnicaImportacaoMBean.importacaoDados}">
						<td colspan="7">
							<table style="width: 100%;">
								<tr>
									<th style="width: 10%;" class="obrigatorio">Dados:</th>
					
									<td style="text-align:center; padding-bottom:20px; width: 90%">
											<h:inputTextarea id="inputAreaDadosDigitados" value="#{cooperacaoTecnicaImportacaoMBean.arquivoDigitado}" rows="30" cols="100" 
											rendered="#{cooperacaoTecnicaImportacaoMBean.importacaoDados}" />
									</td>
								</tr>
								<tr>
									<th></th>
									<td style="text-align: center; font-variant: small-caps; font-size: x-small;"> 
										<a onclick="substituirBarra('formImportar:inputAreaDadosDigitados');" class="link">Substituir "$" por "|" </a>	
										&nbsp;&nbsp;&nbsp;&nbsp;
										<a onclick="substituirContraBarra('formImportar:inputAreaDadosDigitados');" class="link">Substituir "\" por "|" </a>
										&nbsp;&nbsp;&nbsp;&nbsp;
										<a onclick="substituirUnderLine('formImportar:inputAreaDadosDigitados');" class="link">Substituir "#" por "_" </a>
									</td>
								</tr>
							</table>
						</td> 
						
					</c:if> 
				</tr>
	
	
				<tfoot>
					<tr>
						<td colspan="7" align="center">
							
							<h:commandButton value="Importar" action="#{cooperacaoTecnicaImportacaoMBean.realizarInterpretacaoDados}" onclick="return confirm('Confirma Importa��o de Todos os Registros do Arquivo ? ');" />
							
							<h:commandButton value="Visualizar os Registros do Arquivo" 
								action="#{cooperacaoTecnicaImportacaoMBean.visualizarDadosDoArquivo}" 
								rendered="#{cooperacaoTecnicaImportacaoMBean.importandoArquivo}" />
							
							<h:commandButton value="<< Voltar" action="#{buscaCatalogacoesIncompletasMBean.iniciarBuscaAutoridadesIncompletasImportacao}"
									rendered="#{cooperacaoTecnicaImportacaoMBean.cooperacaoAutoridades && cooperacaoTecnicaImportacaoMBean.importouNovasEntidadesExistindoTituloNaoFinalizados }" />	
							
								
							<h:commandButton value="<< Voltar" action="#{catalogaAutoridadesMBean.iniciar}" 
								rendered="#{cooperacaoTecnicaImportacaoMBean.cooperacaoAutoridades && ! cooperacaoTecnicaImportacaoMBean.importouNovasEntidadesExistindoTituloNaoFinalizados }"/> 
								
								
							<h:commandButton value="<< Voltar" action="#{buscaCatalogacoesIncompletasMBean.iniciarBuscaTitulosIncompletosImportacao}"
								rendered="#{cooperacaoTecnicaImportacaoMBean.cooperacaoBibliografica && cooperacaoTecnicaImportacaoMBean.importouNovasEntidadesExistindoTituloNaoFinalizados}" />
						
					
						
							<h:commandButton value="<< Voltar" action="#{pesquisaTituloCatalograficoMBean.telaPesquisaTitulo}" 
								rendered="#{cooperacaoTecnicaImportacaoMBean.cooperacaoBibliografica && ! cooperacaoTecnicaImportacaoMBean.importouNovasEntidadesExistindoTituloNaoFinalizados}"/>
							
							<h:commandButton value="Cancelar" action="#{cooperacaoTecnicaImportacaoMBean.cancelar}" immediate="true" onclick="#{confirm}" />
							
						</td>
					</tr>
				</tfoot>
	
			</table>
		

		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>