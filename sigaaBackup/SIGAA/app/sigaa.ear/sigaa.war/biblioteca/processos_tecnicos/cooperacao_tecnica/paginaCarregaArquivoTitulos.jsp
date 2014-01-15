<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Carregar Arquivo de N�meros de Controle de T�tulos </h2>


<div class="descricaoOperacao">
	<p> P�gina para fazer a carga no sistema dos n�meros de controle usados na coopera��o 
	T�cnica com a Funda��o Get�lio Vargas.</p>
	<p> Os n�meros de controle carregados nessa p�gina ser�o usados de forma sequencial no campo MARC 001.
	Essa � a maneira utilizada para indentificar os arquivos gerados pelo sistema no cat�logo coletivo da FGV.</p>
	<p> <strong>D� prefer�ncia a importar o arquivo fornecido pela FGV, em vez de digitar a faixa dos intervalos, 
	para evitar erros de digita��o.  </strong> </p>
	
</div>


<f:view>

	<a4j:keepAlive beanName="cargaArquivoFGVMBean"></a4j:keepAlive>

	<h:form id="formCarregaArquivoTitulo" enctype="multipart/form-data">

		<c:if test="${fn:length( cargaArquivoFGVMBean.arquivosCarregadosAtivos) > 0}">

			<table class="listagem" style="margin-bottom: 20px; width: 90%;">
				<caption> Arquivos de T�tulos Carregados no Sistema</caption>
				
				<thead>
					<tr>
						<th>Data da Carga </th>
						<th>N�mero Inicial </th>
						<th>N�mero Final </th>
						<th>N�mero Atual </th>
						<th style="text-align: right; ">Quantidade ainda n�o usada</th>
					</tr>
				</thead>
				
				<tbody>
				
					<c:forEach items="#{cargaArquivoFGVMBean.arquivosCarregadosAtivos}" var="arquivo" varStatus="status">
	
	
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						
							<td> <ufrn:format type="dataHora" valor="${arquivo.dataCarga}"/>  </td>
						
							<td> ${cargaArquivoFGVMBean.codigoBibliotecaCatalogaColetivo}${arquivo.numeroInicialSequenciaFormatado} </td>
						
							<td> ${cargaArquivoFGVMBean.codigoBibliotecaCatalogaColetivo}${arquivo.numeroFinalSequenciaFormatado} </td>
							
							<td> ${cargaArquivoFGVMBean.codigoBibliotecaCatalogaColetivo}${arquivo.numeroAtualSequenciaFormatado} </td>
						
							<td style="text-align: right">  ${arquivo.quantidadeNumeroControleNaoUsados} </td>
							
						</tr>
						
					</c:forEach>
				
				</tbody>
			</table>
		
		</c:if>


		<table class="formulario" style="width: 70%">
			<caption> Entre com o arquivo ou digite a faixa de n�meros de controle</caption>
		
		
			<tr style="text-align:center;">
				
				<th>Arquivo:</th>
				
				<td colspan="3" style="margin-bottom:20px; text-align:left;">
					<t:inputFileUpload id="arquivo" value="#{cargaArquivoFGVMBean.arquivo}"/>
				</td>
			</tr>
		
			<tr style="text-align:center;">
				
				<th>N�mero Inicial da Sequ�ncia:</th>
				
				<td style="margin-bottom:20px; text-align:left;">
					<h:inputText value="#{cargaArquivoFGVMBean.numeroInicial}" size="12" maxlength="15" onkeyup="CAPS(this);"/>
					<ufrn:help> Um n�mero neste formato: RN000670211, fornecido pela FGV.</ufrn:help>
				</td>
				
				<th>N�mero Final da Sequ�ncia:</th>
				
				<td style="margin-bottom:20px; text-align:left;">
					<h:inputText value="#{cargaArquivoFGVMBean.numeroFinal}"  size="12" maxlength="15" onkeyup="CAPS(this);"/>
					<ufrn:help> Um n�mero neste formato: RN000675205, fornecido pela FGV.</ufrn:help>
				</td>
				
			</tr>
		
		
			<tfoot>
				<tr>
					<td colspan="4" align="center">	
						<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL } %>">	
							<h:commandButton value="Carregar Arquivo" action="#{cargaArquivoFGVMBean.submeterArquivo}" />
						</ufrn:checkRole>
						<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{cargaArquivoFGVMBean.cancelar}" />
						
					</td>
				</tr>
			</tfoot>
		
		</table>


	</h:form>

	<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>