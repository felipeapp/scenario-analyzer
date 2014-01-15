<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<f:view>
    <h2 class="tituloTabela"><b>Lista de Contatos de Alunos</b></h2>
    <div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Programa:</th>
				<td>
					${relatorioDiscente.matrizCurricular.curso.unidade.nome}			
				</td>
			</tr>	   

			<tr>
				<th>Status: </th>
				<td>
				    <c:if test="${relatorioDiscente.status != 0}">
						${relatorioDiscente.statusDescricao}			
					</c:if>
				    <c:if test="${relatorioDiscente.status == 0}">
						Todos			
					</c:if>					
				</td>		
			</tr>	
	
		</table>
	</div>
	
	<c:set var="varNivel" value=""/>
	<c:set var="quantidade" value="0"/>
	<br/>
		
	<c:forEach items="#{relatorioDiscente.listaDiscente}" var="linha">
		<c:if test="${varNivel != linha.nivel}">
			<c:if test='${varNivel != ""}'>
				<script>document.getElementById("nivel${varNivel}").innerHTML = '${varNivel} ('+${quantidade }+')';</script>				
				</table>   		
			</c:if>			 
			<c:set var="varNivel" value="${linha.nivel}"/>
			<table class="tabelaRelatorioBorda" width="100%" style="border-right: 1px solid #CCC;">
			    <tr class="linhaCinza">
			    	<td colspan="7" align="center" id="nivel${varNivel}"></td>
			    </tr>
				<tr class="linhaCinza">
					<th style="width:60px;">
						Matrícula 
					</th>					
					<th style="width:400px;">
						Nome 
					</th>
					<th style="width:100px; text-align: center;">
						CPF 
					</th>						
					<th style="width:80px;text-align:center;">
						Telefone Fixo 
					</th>
					<th style="width:80px;text-align:center;">
						Celular 
					</th>
					<th style="width:150px;">
						Email
					</th>			
					<c:if test="${relatorioDiscente.status == 0}">
						<th style="width:70px;">Status</th>
					</c:if>					
				</tr>
				<c:set var="quantidade" value="0"/>				
		</c:if> 
			<tr>
				<td style="text-align:center; border-bottom: none;border-right: none;">
					${linha.matricula}
				</td>
				<td style="text-align:left; border-bottom: none;border-right: none;border-left: none;">
					${linha.nome}
				</td>						
				<td style="text-align:center; border-bottom: none;border-right: none;border-left: none;">
					<ufrn:format type="cpf_cnpj" valor="${ linha.cpf_cnpj }"/> 
				</td>						
				<td style="text-align:center; border-bottom: none;border-right: none;border-left: none;">
					${linha.telefone_fixo}
				</td>				
				<td style="text-align:center;border-bottom: none;border-right: none;border-left: none;">
					${linha.telefone_celular}
				</td>					
				<c:if test="${relatorioDiscente.status == 0}">
					<td style ="border-bottom: none;border-left: none;border-right: none;">
						${linha.email}
					</td>						
					<td style="text-align:left;border-bottom: none;border-left: none;">${linha.status }</td>
				</c:if>
				<c:if test="${relatorioDiscente.status != 0}">
					<td style ="border-bottom: none;border-left: none;">
						${linha.email}  
					</td>												
				</c:if>																										
			</tr>	
			<tr>
				<td colspan="7" style ="border-top: none" >
					<strong>&nbsp;&nbsp;Endereço:</strong> ${linha.logradouro}, ${linha.numero}, ${linha.cidade}/${linha.estado}
				</td>					    									
			</tr>	
					<c:set var="quantidade" value="${quantidade + 1}"/>		
	</c:forEach>
			</table>
			<script>document.getElementById("nivel${varNivel}").innerHTML = '${varNivel} ('+${quantidade }+')';</script>
			
    <div align="center"><b> <h:outputText value="#{relatorioDiscente.numeroRegistosEncontrados}"/>&nbsp; Registro(s) Encontrado(s)</b></div>						
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
