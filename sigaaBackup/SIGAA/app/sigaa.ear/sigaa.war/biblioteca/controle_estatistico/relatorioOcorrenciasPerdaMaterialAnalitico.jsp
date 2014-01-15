<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table id="tabela" class="tabelaRelatorioBorda" width="100%" style="margin: auto;">
		
		<thead>
			<tr>
				<th style="width: 10%;">Código de Barras</th>
				<th style="width: 10%;">Situação do Material</th>
				<th style="width: 50%;">Titulo</th>
				<th style="text-align: center; width: 20%;">Situação da Perda</th>
				<th style="text-align: center; width: 10%">Substituído Por</th>
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
					<td colspan="9" style="background-color: #EAEAEA; font-weight: bold;">Usuário: ${resultado.nomeUsuarioPerdeuMaterial}</td>
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
							<td style=" width:25%; border: none;"> Operador Realizou Devolução: </td>
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
									<span style="font-weight: bold;"> Motivo não Entrega do Material: </span> <br/> <span style="font-style: italic;"> ${resultado.motivoNaoEntregaMaterial} </span>
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
							<caption> Dados das Prorrogações do Emprétimo </caption>
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
				<td colspan="3"> TOTAL DE COMUNICAÇÕES EM ABERTO</td>
				<td colspan="3" style="text-align: right;"> ${_abstractRelatorioBiblioteca.quantidadeComunicacoesEmAberto } </td>
			</tr>
			<tr style="font-weight: bold;">
				<td colspan="3"> TOTAL DE COMUNICAÇÕES COM MATERIAL REPOSTO SIMILAR</td>
				<td colspan="3" style="text-align: right;"> ${_abstractRelatorioBiblioteca.quantidadeComunicacoesRepostosSimilar } </td>
			</tr>
			<tr style="font-weight: bold;">
				<td colspan="3"> TOTAL DE COMUNICAÇÕES COM MATERIAL REPOSTO EQUIVALENTE </td>
				<td colspan="3" style="text-align: right;"> ${_abstractRelatorioBiblioteca.quantidadeComunicacoesRepostosEquivalente } </td>
			</tr>
			<tr style="font-weight: bold;">
				<td colspan="3"> TOTAL DE COMUNICAÇÕES COM MATERIAL SUBSTITUÍDO</td>
				<td colspan="3" style="text-align: right;"> ${_abstractRelatorioBiblioteca.quantidadeComunicacoesRepostosESubstituidos } </td>
			</tr>
			<tr style="font-weight: bold;">
				<td colspan="3"> TOTAL DE COMUNICAÇÕES COM MATERIAL NÃO REPOSTO </td>
				<td colspan="3" style="text-align: right;"> ${_abstractRelatorioBiblioteca.quantidadeComunicacoesNaoRepostos } </td>
			</tr>
			<tr style="height: 20px;">
			</tr>
			<tr style="font-weight: bold; color: red;">
				<td colspan="3"> TOTAL DE MATERIAIS PERDIDOS, MAS NÃO BAIXADOS </td>
				<td colspan="3" style="text-align: right;"> ${_abstractRelatorioBiblioteca.quantidadeMateriasNaoBaixados } </td>
			</tr>
			<tr style="height: 20px;">
			</tr>
			<tr style="font-weight: bold;">
				<td colspan="3"> TOTAL DE COMUNICAÇÕES </td>
				<td colspan="3" style="text-align: right;"> ${_abstractRelatorioBiblioteca.quantidadeTotalPerdas} </td>
			</tr>
		</tfoot>
		
	</table>
	
	
	<div style="margin-top:20px;">
		<hr />
		<h4>Legenda:</h4>
		<br/>
		<p> <strong>Em Aberto             </strong> : O usuário comunicou a perda do material, e essa comunicação ainda está em aberto no sistema, ou seja, o material ainda continua emprestado para o usuário.</p>
		<br/>
		<p> <strong>Reposto Similar       </strong> : O usuário entregou um material que é similar ao perdido, não será realizada a baixa no material perdido e o código de barras será mantido.</p>
		<br/>
		<p> <strong>Reposto Equivalente   </strong> : O usuário entregou um material que é equivalente ao perdido, deve-se nesse caso baixar o material perdido de incluir um novo material no acervo.</p>
		<br/>
		<p> <strong>Substituído           </strong> : O usuário entregou um material que é equivalente ao perdido, o material perdido já foi baixado e subsituído por um novo. (Utilizando a operação substituir Exemplar/Fascículo) </p>
		<br/>
		<p> <strong>Não Reposto           </strong> : O usuário não repos o material que havia perdido. </p>
	</div>
	
</f:view>	

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>