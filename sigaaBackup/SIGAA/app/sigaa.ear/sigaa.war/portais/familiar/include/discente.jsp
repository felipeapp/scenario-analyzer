<div id="discente">
	
	<div class="foto">
		<c:if test="${portalFamiliar.discente.idFoto != null}">
			<img src="${ctx}/verFoto?idArquivo=${portalFamiliar.discente.idFoto}&key=${ sf:generateArquivoKey(portalFamiliar.discente.idFoto) }" style="width: 100px; height: 125px"/>
		</c:if>
		<c:if test="${portalFamiliar.discente.idFoto == null}">
			<img src="${ctx}/img/no_picture.png" width="128" height="125"/>
		</c:if>
	</div>		
	
	<div id="dados-discente">
		<h2>Dados do Aluno</h2>
		<table>
			<tr>
				<td> Nome: </td>
				<td> ${portalFamiliar.discente.nome} </td>
			</tr>				
			<tr>
				<td> Matrícula: </td>
				<td> ${portalFamiliar.discente.matricula} </td>
			</tr>
			<tr>
				<td> Curso: </td>
				<td> ${portalFamiliar.discente.curso.nome} </td>
			</tr>
			<tr>
				<td> Nível: </td>
				<td> ${portalFamiliar.discente.nivelDesc} </td>
			</tr>
			<tr>
				<td> Status: </td>
				<td> ${portalFamiliar.discente.statusString} </td>
			</tr>
			<tr>
				<td> E-Mail: </td>
				<td>
					<ufrn:format type="texto" valor="${portalFamiliar.discente.pessoa.email}" length="18" />
				</td>
			</tr>
			<tr>
				<td> Entrada: </td>
				<td> ${portalFamiliar.discente.anoIngresso}</td>
			</tr>
			<tr>
				<td> Ingresso: </td>
				<td> ${portalFamiliar.discente.formaIngresso.descricao}</td>
			</tr>
			
		</table>				
	</div>
	
	<div style="margin-top: 5px;">		
		<h2>Responsáveis do Aluno</h2>
		
		<c:forEach items="#{portalFamiliar.responsaveis}" var="r" varStatus="status">
			<p>${r.usuario.nome}</p>
		</c:forEach>	
	
	</div>	

	<div class="clear"></div>
	
	
</div>

