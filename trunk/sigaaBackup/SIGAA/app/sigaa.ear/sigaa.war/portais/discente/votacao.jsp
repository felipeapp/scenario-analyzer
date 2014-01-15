<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
<!--
table.subFormulario th{
	font-weight: bold;
}

	.botaoConfirma {
		height: 30px;
		width: 70px;
		background: #49BB5D;
		font-weight: bold;
		cursor: pointer;
	}
	
	.botaoBranco {
		height: 30px;
		width: 70px;
		background: #FFF;
		font-weight: bold;
		cursor: pointer;
	}
	
	.botaoNulo {
		height: 30px;
		width: 70px;
		background: #FEA23B;		
		font-weight: bold;
		cursor: pointer;
	}

-->
</style>

<script type="text/javascript">

function exibirCandidato(eleicao,chapa) {

	getContent('/sigaa/portais/discente/include_votacao.jsf', 'divVotacao', 'idEleicao=' + eleicao + '&chapa=' + chapa );

}

function atualizaChapa(eleicao, chapa){
if( chapa != null ){
		getContent('/sigaa/portais/discente/include_votacao.jsf', 'divVotacao', 'idEleicao=' + eleicao + '&chapa=' + chapa );
	}
	else{
		getContent('/sigaa/portais/discente/include_votacao.jsf', 'divVotacao', 'idEleicao=' + eleicao + '&chapa=-1');
	}
}

function setaChapa(chapa){
	$('formVotacao:txtChapa').value = chapa;
	atualizaChapa($('formVotacao:id_eleicao').value, chapa);
}

</script>

<f:view>

	<h2> Votação </h2>

	<h:outputText value="#{voto.create}"/>
	
<h:form id="formVotacao">
<h:inputHidden value="#{voto.eleicao.id}" id="id_eleicao"/>

	<table align="center" class="formulario" width="75%">
	<caption>${voto.eleicao.titulo}</caption>
		<thead></thead>
		
		<tbody>	
		
		<tr>
			<td>
				<div id="divVotacao" style="width: 400px; height: 250px">
					<c:set var="limpar" value="true" />
					<%@include file="/portais/discente/include_votacao.jsp"%>
				</div>
			</td>
			
			<td style="vertical-align: top">
			<table>
				<tr>
				<td style="font-size: 14px" align="center"> 
					<b>Escolha seu candidato:</b>		
					<h:inputText style="font-size: 20px; text-align: center; padding: 4px;" 
						value="#{voto.chapa}" 
						onchange="exibirCandidato(#{voto.eleicao.id},this.value)" 
						onkeyup="exibirCandidato(#{voto.eleicao.id},this.value)" 
						id="txtChapa" maxlength="1" size="1"/>
				</td>
				</tr>
				<tr>
				<td>
				<div style="background-color: #666467">
					
					<table align="center">
						<tr>
							<td> <img src="${ctx}/img/eleicao/um.jpg" onclick="setaChapa(1)" style="cursor: pointer;"/> </td>
							<td> <img src="${ctx}/img/eleicao/dois.jpg" onclick="setaChapa(2)" style="cursor: pointer;"/> </td>
							<td> <img src="${ctx}/img/eleicao/tres.jpg" onclick="setaChapa(3)" style="cursor: pointer;"/> </td>
						</tr>
						<tr>
							<td> <img src="${ctx}/img/eleicao/quatro.jpg" onclick="setaChapa(4)" style="cursor: pointer;"/> </td>
							<td> <img src="${ctx}/img/eleicao/cinco.jpg" onclick="setaChapa(5)" style="cursor: pointer;"/> </td>
							<td> <img src="${ctx}/img/eleicao/seis.jpg" onclick="setaChapa(6)" style="cursor: pointer;"/> </td>
						</tr>
						<tr>
							<td> <img src="${ctx}/img/eleicao/sete.jpg" onclick="setaChapa(7)" style="cursor: pointer;"/> </td>
							<td> <img src="${ctx}/img/eleicao/oito.jpg" onclick="setaChapa(8)" style="cursor: pointer;"/> </td>
							<td> <img src="${ctx}/img/eleicao/nove.jpg" onclick="setaChapa(9)" style="cursor: pointer;"/> </td>
						</tr>
						<tr>
							<td>  </td>
							<td> <img src="${ctx}/img/eleicao/zero.jpg" onclick="setaChapa(0)" style="cursor: pointer;"/> </td>
							<td>  </td>
						</tr>
					
					</table>
					
					<table align="center">
						<tr>
							<td><h:commandButton styleClass="botaoBranco" value="Branco" action="#{voto.votarBranco}"/></td>
							<td>
								<input type="button" class="botaoNulo" value="Corrige" onclick="setaChapa(null)"/>
							</td>
							<td><h:commandButton styleClass="botaoConfirma" value="Confirmar" action="#{voto.votar}"/></td>
						</tr>
					</table>
				
				</div>
				</td>
				</tr>
				
			</table>
			</td>
				

		</tr>

		
		
		</tbody>
	
	</table>	
</h:form>	
	<%-- 
	<c:set var="candidatos" value="${voto.eleicao.candidatos}" />

	<table align="center" class="formulario">
	<caption>${voto.eleicao.titulo}</caption>
		<thead></thead>
		
		<tbody>
	
		<tr>
			
			<c:forEach var="candidato" items="${candidatos}">
			
				<td style="padding: 20px;">
				
					<table class="subFormulario" width="300">
						<caption><c:out value="${candidato.servidor.nome}"/></caption>
						
						<tr>
							<td colspan="2" align="center">
							<img src="${ctx}/verFoto?idFoto=<c:out value="${candidato.idFoto}"/>" border="0" width="100" heigth="107">
							</td>
						</tr>
						
						<tr>
							<th>Chapa:</th>
							<td><c:out value="${candidato.chapa}"/></td>
						</tr>

						<tr>
							<th>Descrição:</th>
							<td><c:out value="${candidato.descricao}"/></td>
						</tr>
						
						<tr align="center">
							<td colspan="2"> 
								
								<h:form>
									<input type="hidden" name="id" value="${candidato.id}"/>
									<h:commandButton value=" Votar ! " action="#{voto.votar}" id="btnVotar" style="cursor: pointer;"/>
								</h:form>
								
							</td>
						</tr>
						

					
					</table>
					
				</td>
				
			</c:forEach>

		
		</tr>
		
		<tr>
		<td>
		
			<h:form id="formBranco">
				<h:inputHidden value="#{voto.eleicao.id}"/>
			
			<table align="center" width="250px;">
				<tr>
					<td>
						<h:commandButton styleClass="botaoBranco" value="Branco" action="#{voto.votarBranco}"/>
					</td>
					
					<td>
						<h:commandButton styleClass="botaoNulo" value="Nulo" action="#{voto.votarBranco}"/>
					</td>			
				</tr>
			
			</table>
			</h:form>
			
		</td>
		</tr>
		
		
		</tbody>
	
	</table>
	--%>
	
	

	<br/>
	<br/>
	<br/>		


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

