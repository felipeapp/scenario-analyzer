<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<a4j:keepAlive beanName="configuraInformacoesAreasCNPqBibliotecaMBean"></a4j:keepAlive>
	
	<h2><ufrn:subSistema /> &gt; Bibliotecas e Suas Classifica��es Bibliogr�ficas </h2>

	<div class="descricaoOperacao"> 
	    <p> Caro Usu�rio, </p>
	    <p> Nesta p�gina � poss�vel configurar as informa��es sobre as �reas CNPq utilizadas na biblioteca. </p>
	    <p> Por conven��o, algumas nomenclatura utilizadas na biblioteca s�o diferentes da nomenclatura oficial. 
	    Nesta op��o � poss�vel configurar para o sistema mostrar de acordo com as nomenclatura que os bibliotec�rios est�o acostumados a utilizar. </p>
	</div>
	
	

	<h:form id="formConfiguraRelacionamentoBibliotecaClassificacoes">

		<table class="formulario" width="100%">
			<caption class="listagem">Informa��es das �reas CNPq Utilizadas na Biblioteca</caption>
			<thead>
				<tr>
					<th>Sigla Oficial</th>
					<th>Sigla da Biblioteca</th>
					<th>Nome Oficial</th>
					<th>Nome da Biblioteca</th>
				</tr>
			</thead>
			
			
			<c:forEach items="#{configuraInformacoesAreasCNPqBibliotecaMBean.informacoesAreasBiblioteca}" var="infoArea" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					
					<td style="width: 10%;">${infoArea.area.sigla}</td>
					<td style="width: 10%;"> <h:inputText value="#{infoArea.sigla}" size="10" maxlength="10" /> </td>
					
					<td style="width: 40%;">${infoArea.area.nome}</td>
					<td style="width: 40%;"> <h:inputText value="#{infoArea.nome}" size="60" maxlength="120" /> </td>
				</tr>
			</c:forEach>
			
			
			<tfoot>
				<tr>
					<td colspan="4" style="text-align: center;">
						<h:commandButton value="Atualizar" action="#{configuraInformacoesAreasCNPqBibliotecaMBean.atualizarInformacoesArea}" 
						onclick="return confirm('Confirma a atualiza��o das Informa��es das �reas do CNPq utilizadas na Biblioteca ?');"/>
						<h:commandButton value="Cancelar" action="#{configuraInformacoesAreasCNPqBibliotecaMBean.cancelar}" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
			
		</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>