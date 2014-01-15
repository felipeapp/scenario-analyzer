<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table id="tabela" class="tabelaRelatorioBorda" width="100%" style="margin: auto;">
		
		<thead>
			<tr>
				<th style="width: 10%;">C�digo de Barras</th>
				<th style="width: 10%;">Situa��o do Material</th>
				<th style="width: 50%;">Titulo</th>
				<th style="text-align: center; width: 20%;">Situa��o da Perda</th>
				<th style="text-align: center; width: 10%">Substitu�do Por</th>
			</tr>
		</thead>
		
		<c:set var="_biblioteca_ocorrencia" value="" scope="request" />
		<c:set var="_usuario_ocorrencia" value="" scope="request" />
		
		
		<c:forEach var="resultado" items="${_abstractRelatorioBiblioteca.resultadoAnalitico}">
			
			
			<c:if test="${ _biblioteca_ocorrencia != resultado.descricaoBiblioteca}">
				<c:set var="_biblioteca_ocorrencia" value="${resultado.descricaoBiblioteca}" scope="request"/>
				<c:set var="_usuario_ocorrencia" value=" " scope="request"/>
				<tr style="height: 30px;">
				</tr>
				<tr>
					<td colspan="9" style="background-color: #C0C0C0; font-weight: bold;">${resultado.descricaoBiblioteca}</td>
				</tr>
			</c:if>
			 
			<c:if test="${ _usuario_ocorrencia != resultado.nomeUsuarioPerdeuMaterial}">
				<c:set var="_usuario_ocorrencia" value="${resultado.nomeUsuarioPerdeuMaterial}" scope="request"/>
				<tr style="height: 20px;">
				</tr>
				<tr>
					<td colspan="9" style="background-color: #EAEAEA; font-weight: bold;">Usu�rio: ${resultado.nomeUsuarioPerdeuMaterial}</td>
				</tr>
			</c:if>
			
			<tr style="height: 20px;">
			
			</tr>
				
			<tr>
				<td style="text-align: center; background-color: #EAEAEA;"> ${resultado.codigoBarras} </td>
				<td style="text-align: center; background-color: #EAEAEA; ${ ! resultado.baixado && ! resultado.comunicaoEmAberto && ! resultado.usuarioReposMaterialSimilar ? 'color:red' : ''}"> ${resultado.descricaoSituacaoMaterial}   </td>
				<c:if test="${resultado.baixado}">
					<td style="text-align: center; background-color: #EAEAEA;"> ${resultado.informacaoMaterialBaixado}   </td>
				</c:if>
				<c:if test="${! resultado.baixado}">
					<td style="text-align: center; background-color: #EAEAEA;"> ${resultado.descricaoTitulo}   </td>
				</c:if>
				
				<td style="font-weight: bold; text-align: center; background-color: #EAEAEA; ${resultado.usuarioNaoReposMaterial == true ? 'color: red;' :  ( resultado.usuarioReposMaterial == true ? 'color: green;' : '' )  }"> 
					${resultado.situacaoDevolucao.descricao}   
				</td>
				<td style="text-align: center;background-color: #EAEAEA; "> ${resultado.codigoBarrasSubstituidor}   </td>		
				
			</tr>
			
			<c:if test="${not empty resultado.usuarioDevolveuEmprestimo && resultado.dataDevolucao != null}">	
				<tr>
					<td colspan="5"> 
						<table style="width: 95% ; margin: auto;"> 
						<tr>
							<td style=" width:25%; border: none;"> Operador Realizou Devolu��o: </td>
							<td style=" width:40%; border: none;"> ${resultado.usuarioDevolveuEmprestimo} </td>
							<td style=" width:5%;  border: none;"> Em: </td>
							<td style=" width:30%; border: none;"> <ufrn:format type="dataHora" valor="${resultado.dataDevolucao}" /> </td>
						</tr>
						</table>
					
					</td>
				</tr>
			</c:if>
			
			<c:if test="${resultado.usuarioNaoReposMaterial}">	
				<tr>
					<td colspan="5"> 
						<table style="width: 95% ; margin: auto;"> 
							<tr>
								<td style="border: none;">
									<span style="font-weight: bold;"> Motivo n�o Entrega do Material: </span> <br/> <span style="font-style: italic;"> ${resultado.motivoNaoEntregaMaterial} </span>
								</td>
							</tr>
						</table> 
					</td>
				</tr>
			</c:if>	
			
			<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}">	
				<tr>
					<td colspan="5">
						<table style="width: 95% ; margin: auto;"> 
							<caption> Dados das Prorroga��es do Empr�timo </caption>
							<c:forEach var="dadosProrrogacao" items="${resultado.dadosProrrogacoes}">
								<tr>
									<th style="border: none;">Prazo Anterior</th>
									<td style="border: none;"> <ufrn:format type="dataHora" valor="${dadosProrrogacao.dataIncial}" />   </td>
									<th style="border: none;">Novo Prazo</th>
									<td style="border: none;"> <ufrn:format type="dataHora" valor="${dadosProrrogacao.dataFinal}" /> </td>  
									<th style="border: none;">Operador</th>
									<td style="border: none;">  ${dadosProrrogacao.usuarioCadastrouProrrogacao} </td>
								</tr>
								<tr>
									<th style="border: none;">Motivo:</th>
									<td style=" border: none; width: 80%;" colspan="5"> ${dadosProrrogacao.justificacativa} </td>
								</tr>
							</c:forEach>
					</table>
					</td>
				</tr>
			</c:if>
			
			
		</c:forEach>
		
		<tfoot>
			<tr style="height: 50px;">
			</tr>
			<tr style="font-weight: bold;">
				<td colspan="3"> TOTAL DE COMUNICA��ES EM ABERTO</td>
				<td colspan="3" style="text-align: right;"> ${_abstractRelatorioBiblioteca.quantidadeComunicacoesEmAberto } </td>
			</tr>
			<tr style="font-weight: bold;">
				<td colspan="3"> TOTAL DE COMUNICA��ES COM MATERIAL REPOSTO SIMILAR</td>
				<td colspan="3" style="text-align: right;"> ${_abstractRelatorioBiblioteca.quantidadeComunicacoesRepostosSimilar } </td>
			</tr>
			<tr style="font-weight: bold;">
				<td colspan="3"> TOTAL DE COMUNICA��ES COM MATERIAL REPOSTO EQUIVALENTE </td>
				<td colspan="3" style="text-align: right;"> ${_abstractRelatorioBiblioteca.quantidadeComunicacoesRepostosEquivalente } </td>
			</tr>
			<tr style="font-weight: bold;">
				<td colspan="3"> TOTAL DE COMUNICA��ES COM MATERIAL SUBSTITU�DO</td>
				<td colspan="3" style="text-align: right;"> ${_abstractRelatorioBiblioteca.quantidadeComunicacoesRepostosESubstituidos } </td>
			</tr>
			<tr style="font-weight: bold;">
				<td colspan="3"> TOTAL DE COMUNICA��ES COM MATERIAL N�O REPOSTO </td>
				<td colspan="3" style="text-align: right;"> ${_abstractRelatorioBiblioteca.quantidadeComunicacoesNaoRepostos } </td>
			</tr>
			<tr style="height: 20px;">
			</tr>
			<tr style="font-weight: bold; color: red;">
				<td colspan="3"> TOTAL DE MATERIAIS PERDIDOS, MAS N�O BAIXADOS </td>
				<td colspan="3" style="text-align: right;"> ${_abstractRelatorioBiblioteca.quantidadeMateriasNaoBaixados } </td>
			</tr>
			<tr style="height: 20px;">
			</tr>
			<tr style="font-weight: bold;">
				<td colspan="3"> TOTAL DE COMUNICA��ES </td>
				<td colspan="3" style="text-align: right;"> ${_abstractRelatorioBiblioteca.quantidadeTotalPerdas} </td>
			</tr>
		</tfoot>
		
	</table>
	
	
	<div style="margin-top:20px;">
		<hr />
		<h4>Legenda:</h4>
		<br/>
		<p> <strong>Em Aberto             </strong> : O usu�rio comunicou a perda do material, e essa comunica��o ainda est� em aberto no sistema, ou seja, o material ainda continua emprestado para o usu�rio.</p>
		<br/>
		<p> <strong>Reposto Similar       </strong> : O usu�rio entregou um material que � similar ao perdido, n�o ser� realizada a baixa no material perdido e o c�digo de barras ser� mantido.</p>
		<br/>
		<p> <strong>Reposto Equivalente   </strong> : O usu�rio entregou um material que � equivalente ao perdido, deve-se nesse caso baixar o material perdido de incluir um novo material no acervo.</p>
		<br/>
		<p> <strong>Substitu�do           </strong> : O usu�rio entregou um material que � equivalente ao perdido, o material perdido j� foi baixado e subsitu�do por um novo. (Utilizando a opera��o substituir Exemplar/Fasc�culo) </p>
		<br/>
		<p> <strong>N�o Reposto           </strong> : O usu�rio n�o repos o material que havia perdido. </p>
	</div>
	
</f:view>	

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>